package com.chatbuddy.dto.message;

import java.time.LocalDateTime;
import java.util.List;

public record MessageResponseDto(Long id, String prompt, String response,
                                 List<String> adminMessage, LocalDateTime timestamp) {
}
