package oleg.turyk.test.mapper;

import oleg.turyk.test.config.MapperConfig;
import oleg.turyk.test.dto.chat.ChatDetailsResponseDto;
import oleg.turyk.test.dto.chat.ChatResponseDto;
import oleg.turyk.test.model.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ChatMapper {
    ChatResponseDto toDto(Chat chat);

    @Mapping(target = "history", source = "messages")
    ChatDetailsResponseDto toDetailsDto(Chat chat);
}
