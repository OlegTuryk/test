package oleg.turyk.test.mapper;

import oleg.turyk.test.config.MapperConfig;
import oleg.turyk.test.dto.user.UserRegistrationRequestDto;
import oleg.turyk.test.dto.user.UserRegistrationResponseDto;
import oleg.turyk.test.model.User;
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
