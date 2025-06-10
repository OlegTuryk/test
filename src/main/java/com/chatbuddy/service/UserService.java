package com.chatbuddy.service;

import java.util.List;
import com.chatbuddy.dto.user.UserRegistrationRequestDto;
import com.chatbuddy.dto.user.UserRegistrationResponseDto;
import com.chatbuddy.exception.RegistrationException;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException;

    void deleteUser(Long id);

    List<UserRegistrationResponseDto> getUsers(Pageable pageable);
}
