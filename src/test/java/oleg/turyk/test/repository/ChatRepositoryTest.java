package oleg.turyk.test.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Optional;
import oleg.turyk.test.model.Chat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/insert/add-chats.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/delete/delete-chats.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ChatRepositoryTest {
    private static final Chat CHAT = new Chat();
    @Autowired
    private ChatRepository chatRepository;

    @BeforeAll
    public static void setUp() {
        CHAT.setId(1L);
        CHAT.setTelegramChatId(123456789L);
        CHAT.setFirstName("Alice");
        CHAT.setUsername("alice123");
        CHAT.setMessages(new ArrayList<>());
    }

    @Test
    @DisplayName("Find chat by telegramChatId id")
    public void findByTelegramChatId_ValidTelegramChatId_ReturnChat() {
        Optional<Chat> actualOptional = chatRepository
                .findByTelegramChatId(CHAT.getTelegramChatId());
        assertTrue(actualOptional.isPresent());
        Chat actual = actualOptional.get();
        assertEquals(actual, CHAT);
    }
}
