package com.chatbuddy.dto.chat;

import java.util.List;
import com.chatbuddy.dto.message.MessageResponseDto;

public record ChatDetailsResponseDto(Long id, Long telegramChatId, String firstName,
                                     String username, List<MessageResponseDto> history) {
}
