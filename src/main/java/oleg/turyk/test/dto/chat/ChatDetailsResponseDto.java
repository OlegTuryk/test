package oleg.turyk.test.dto.chat;

import java.util.List;
import oleg.turyk.test.dto.message.MessageResponseDto;

public record ChatDetailsResponseDto(Long id, Long telegramChatId, String firstName,
                                     String username, List<MessageResponseDto> history) {
}
