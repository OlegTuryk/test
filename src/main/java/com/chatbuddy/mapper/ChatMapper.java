package com.chatbuddy.mapper;

import com.chatbuddy.config.MapperConfig;
import com.chatbuddy.dto.chat.ChatDetailsResponseDto;
import com.chatbuddy.dto.chat.ChatResponseDto;
import com.chatbuddy.model.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ChatMapper {
    ChatResponseDto toDto(Chat chat);

    @Mapping(target = "history", source = "messages")
    ChatDetailsResponseDto toDetailsDto(Chat chat);
}
