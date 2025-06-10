package com.chatbuddy.mapper;

import java.time.LocalDateTime;
import com.chatbuddy.config.MapperConfig;
import com.chatbuddy.dto.message.MessageResponseDto;
import com.chatbuddy.model.Message;
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
