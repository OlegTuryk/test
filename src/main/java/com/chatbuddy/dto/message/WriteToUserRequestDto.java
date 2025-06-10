package com.chatbuddy.dto.message;

import jakarta.validation.constraints.NotBlank;

public record WriteToUserRequestDto(@NotBlank String message) {
}
