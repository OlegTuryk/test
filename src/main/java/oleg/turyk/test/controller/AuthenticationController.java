package oleg.turyk.test.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oleg.turyk.test.dto.user.UserInformationDto;
import oleg.turyk.test.dto.user.UserLoginRequestDto;
import oleg.turyk.test.dto.user.UserLoginResponseDto;
import oleg.turyk.test.dto.user.UserRegistrationRequestDto;
import oleg.turyk.test.exception.RegistrationException;
import oleg.turyk.test.service.AuthenticationService;
import oleg.turyk.test.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {
        return authenticationService.authenticate(userLoginRequestDto);
    }

    @PostMapping("/register")
    public UserInformationDto register(
            @RequestBody @Valid UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException {
        return userService.register(userRegistrationRequestDto);
    }
}
