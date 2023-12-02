package oleg.turyk.test.service;

import java.util.List;
import oleg.turyk.test.dto.chat.ChatDetailsResponseDto;
import oleg.turyk.test.dto.chat.ChatResponseDto;
import oleg.turyk.test.dto.message.MessageDto;
import oleg.turyk.test.model.Chat;
import oleg.turyk.test.model.Message;
import org.springframework.data.domain.Pageable;

public interface ChatService {
    void saveMessage(Message message);

    Chat findChat(org.telegram.telegrambots.meta.api.objects.Chat chat);

    List<MessageDto> getHistory(Long chatId);

    List<ChatResponseDto> getChats(Pageable pageable);

    ChatDetailsResponseDto getChatDetails(Long id);

    void deleteChat(Long id);

    void deleteMessage(Long id);

    Message findLastMessageByTelegramChatId(Long telegramChatId);
}
