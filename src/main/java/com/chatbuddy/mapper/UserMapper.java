package com.chatbuddy.mapper;

import com.chatbuddy.config.MapperConfig;
import com.chatbuddy.dto.user.UserRegistrationRequestDto;
import com.chatbuddy.dto.user.UserRegistrationResponseDto;
import com.chatbuddy.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegistrationResponseDto toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);
}
