package oleg.turyk.test.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import oleg.turyk.test.dto.message.MessageDto;
import oleg.turyk.test.dto.message.WriteToUserRequestDto;
import oleg.turyk.test.mapper.MessageMapper;
import oleg.turyk.test.model.Chat;
import oleg.turyk.test.service.ChatGptService;
import oleg.turyk.test.service.ChatService;
import oleg.turyk.test.service.impl.ChatGptServiceImpl;
import oleg.turyk.test.service.impl.ChatServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class ChatBot extends TelegramLongPollingBot {
    private final String botName;
    private final ChatGptService chatGptServiceImpl;
    private final ChatService chatService;
    private final MessageMapper messageMapper;

    public ChatBot(@Value("${telegram.bot.token}") String botToken,
                   @Value("${telegram.bot.name}") String botName,
                   ChatGptServiceImpl chatGptServiceImpl,
                   ChatServiceImpl chatService,
                   MessageMapper messageMapper) {
        super(botToken);
        this.botName = botName;
        this.chatGptServiceImpl = chatGptServiceImpl;
        this.chatService = chatService;
        this.messageMapper = messageMapper;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String prompt = message.getText();
            List<MessageDto> messageDtoList = chatService.getHistory(chatId);
            messageDtoList.add(new MessageDto("user", prompt));
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String history = objectMapper.writeValueAsString(messageDtoList);
                String response = chatGptServiceImpl.chatGpt(history);
                Chat chat = chatService.findChat(message.getChat());
                oleg.turyk.test.model.Message savedMessage
                        = messageMapper.toEntity(prompt, response, chatId);
                savedMessage.setChat(chat);
                chatService.saveMessage(savedMessage);
                sendMessage(chatId, response);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Can't convert messages to json.", e);
            }
        }
    }

    public void writeToUser(Long telegramChatId, WriteToUserRequestDto message) {
        sendMessage(telegramChatId, message.message());
        oleg.turyk.test.model.Message updateMessage
                = chatService.findLastMessageByTelegramChatId(telegramChatId);
        updateMessage.getAdminMessage().add(message.message());
        //updateMessage.setAdminMessage();
        chatService.saveMessage(updateMessage);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private void sendMessage(Long telegramChatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramChatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't send message.", e);
        }
    }
}
