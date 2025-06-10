package com.chatbuddy.dto.chat;

public record ChatResponseDto(Long id, Long telegramChatId, String firstName, String username) {
}
