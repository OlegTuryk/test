package oleg.turyk.test.service;

import lombok.RequiredArgsConstructor;
import oleg.turyk.test.dto.user.UserLoginRequestDto;
import oleg.turyk.test.dto.user.UserLoginResponseDto;
import oleg.turyk.test.security.JwtUtil;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final JwtUtil jwtUtil;

    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        String token = jwtUtil.generateToken(requestDto.email());
        return new UserLoginResponseDto(token);
    }
}
