package oleg.turyk.test.service;

import oleg.turyk.test.dto.user.UserInformationDto;
import oleg.turyk.test.dto.user.UserRegistrationRequestDto;
import oleg.turyk.test.exception.RegistrationException;

public interface UserService {
    UserInformationDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException;
}
