package oleg.turyk.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import oleg.turyk.test.dto.user.UserLoginRequestDto;
import oleg.turyk.test.dto.user.UserLoginResponseDto;
import oleg.turyk.test.security.JwtUtil;
import oleg.turyk.test.service.impl.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthenticateServiceTest {
    private static final UserLoginRequestDto USER_LOGIN_REQUEST_DTO
            = new UserLoginRequestDto("bob@example.com", "password123");
    private static final UserLoginResponseDto USER_LOGIN_RESPONSE_DTO
            = new UserLoginResponseDto("token");
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Authenticate")
    public void authenticate_validEmail_ReturnToken() {
        when(jwtUtil.generateToken(USER_LOGIN_REQUEST_DTO.email())).thenReturn("token");
        UserLoginResponseDto actual = authenticationService.authenticate(USER_LOGIN_REQUEST_DTO);
        assertNotNull(actual);
        assertEquals(USER_LOGIN_RESPONSE_DTO, actual);
    }
}
