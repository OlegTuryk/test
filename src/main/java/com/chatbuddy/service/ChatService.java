package com.chatbuddy.service;

import java.util.List;
import com.chatbuddy.dto.chat.ChatDetailsResponseDto;
import com.chatbuddy.dto.chat.ChatResponseDto;
import com.chatbuddy.dto.message.MessageDto;
import com.chatbuddy.dto.message.MessageResponseDto;
import com.chatbuddy.model.Chat;
import com.chatbuddy.model.Message;
import org.springframework.data.domain.Pageable;

public interface ChatService {
    MessageResponseDto saveMessage(Message message);

    Chat findChat(org.telegram.telegrambots.meta.api.objects.Chat chat);

    List<MessageDto> getHistory(Long chatId);

    List<ChatResponseDto> getChats(Pageable pageable);

    ChatDetailsResponseDto getChatDetails(Long id);

    void deleteChat(Long id);

    void deleteMessage(Long id);

    Message findLastMessageByTelegramChatId(Long telegramChatId);
}
