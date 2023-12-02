package oleg.turyk.test.dto.message;

import java.time.LocalDateTime;

public record MessageResponseDto(Long id, String prompt, String response, LocalDateTime timestamp) {
}
