package oleg.turyk.test.service.impl;

import jakarta.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import oleg.turyk.test.service.ChatGptService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatGptServiceImpl implements ChatGptService {
    private static final String CHAT_GPT_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String BEARER = "Bearer ";
    private static final String MODEL_KEY = "model";
    private static final String MESSAGES_KEY = "messages";
    private static final String CONTENT_TEXT = "content";
    private static final int CONTENT_FIELD_OFFSET = 11;
    private final URI chatGptUri;
    @Value("${openai.api.key}")
    private String chatGptToken;

    public ChatGptServiceImpl() throws URISyntaxException {
        this.chatGptUri = new URI(CHAT_GPT_URL);
    }

    public String chatGpt(String prompt) {
        try {
            HttpURLConnection connection = getHttpUrlConnection(prompt);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in
                        = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                connection.disconnect();
                return extractMessageFromJsonResponse(response.toString());
            } else {
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractMessageFromJsonResponse(String response) {
        int start = response.indexOf(CONTENT_TEXT) + CONTENT_FIELD_OFFSET;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }

    @NotNull
    private HttpURLConnection getHttpUrlConnection(String prompt) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) chatGptUri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty(AUTHORIZATION_HEADER, BEARER + chatGptToken);
        connection.setRequestProperty(CONTENT_TYPE_HEADER, APPLICATION_JSON);
        String body = String.format("{\"%s\":\"%s\",\"%s\":%s}",
                MODEL_KEY, MODEL, MESSAGES_KEY, prompt);
        connection.setDoOutput(true);
        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            writer.write(body);
            writer.flush();
        }
        return connection;
    }
}
