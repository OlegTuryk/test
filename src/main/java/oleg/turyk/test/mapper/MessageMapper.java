package oleg.turyk.test.mapper;

import java.time.LocalDateTime;
import oleg.turyk.test.config.MapperConfig;
import oleg.turyk.test.dto.message.MessageResponseDto;
import oleg.turyk.test.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, imports = {LocalDateTime.class})
public interface MessageMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    @Mapping(target = "chat.id", source = "chatId")
    @Mapping(target = "adminMessage", ignore = true)
    Message toEntity(String prompt, String response, Long chatId);

    MessageResponseDto toDto(Message message);
}
