package oleg.turyk.test.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import oleg.turyk.test.dto.chat.ChatDetailsResponseDto;
import oleg.turyk.test.dto.chat.ChatResponseDto;
import oleg.turyk.test.dto.message.MessageResponseDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:database/insert/add-chats.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/insert/add-message.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/delete/delete-messages.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "classpath:database/delete/delete-chats.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ChatControllerTest {
    protected static MockMvc mockMvc;
    private static final Long ID = 1L;
    private static final String ADMIN = "ADMIN";
    private static final String CHATS_ENDPOINT = "/chats";
    private static final MessageResponseDto MESSAGE_RESPONSE_DTO
            = new MessageResponseDto(ID, "Hello", "Hi",
                    new ArrayList<>(),
            LocalDateTime.of(2023, 1, 1, 1, 1, 1));
    private static final ChatDetailsResponseDto CHAT_DETAILS_RESPONSE_DTO
            = new ChatDetailsResponseDto(ID, 123456789L,
            "Alice", "alice123",
            List.of(MESSAGE_RESPONSE_DTO));
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = ADMIN, roles = {ADMIN})
    @DisplayName("Get chat details")
    void getChatDetails_validChatId_returnChatDetails() throws Exception {
        MvcResult result = mockMvc.perform(
                        get(CHATS_ENDPOINT + "/" + ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        ChatDetailsResponseDto actual = objectMapper.readValue(result
                .getResponse().getContentAsString(), ChatDetailsResponseDto.class);

        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(CHAT_DETAILS_RESPONSE_DTO, actual, "id"));
    }

    @Test
    @WithMockUser(username = ADMIN, roles = {ADMIN})
    @DisplayName("Delete chat by valid id")
    void deleteChat_validId_successful() throws Exception {
        mockMvc.perform(delete(CHATS_ENDPOINT + "/" + ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = ADMIN, roles = {ADMIN})
    @DisplayName("Delete message by valid id")
    void deleteMessage_validId_successful() throws Exception {
        mockMvc.perform(delete(CHATS_ENDPOINT + "/messages/" + ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = ADMIN, roles = {ADMIN})
    @DisplayName("Get chats")
    void getChats_getAllChats() throws Exception {
        List<ChatResponseDto> expected = List.of(new ChatResponseDto(ID,
                CHAT_DETAILS_RESPONSE_DTO.telegramChatId(),
                CHAT_DETAILS_RESPONSE_DTO.firstName(),
                CHAT_DETAILS_RESPONSE_DTO.username()), new ChatResponseDto(2L, 987654321L,
                "Bob", "bob123"));
        MvcResult result = mockMvc.perform(
                        get(CHATS_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        List<ChatResponseDto> actual = List.of(objectMapper.readValue(result
                .getResponse().getContentAsString(), ChatResponseDto[].class));
        assertEquals(actual, expected);
    }
}
