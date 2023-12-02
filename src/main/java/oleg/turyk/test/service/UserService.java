package oleg.turyk.test.service;

import java.util.List;
import oleg.turyk.test.dto.user.UserRegistrationRequestDto;
import oleg.turyk.test.dto.user.UserRegistrationResponseDto;
import oleg.turyk.test.exception.RegistrationException;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException;

    void deleteUser(Long id);

    List<UserRegistrationResponseDto> getUsers(Pageable pageable);
}
