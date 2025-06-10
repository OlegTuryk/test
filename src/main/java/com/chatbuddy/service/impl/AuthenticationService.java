package com.chatbuddy.service.impl;

import lombok.RequiredArgsConstructor;
import com.chatbuddy.dto.user.UserLoginRequestDto;
import com.chatbuddy.dto.user.UserLoginResponseDto;
import com.chatbuddy.security.JwtUtil;
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
