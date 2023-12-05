package oleg.turyk.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import oleg.turyk.test.model.Role;
import oleg.turyk.test.model.User;
import oleg.turyk.test.repository.UserRepository;
import oleg.turyk.test.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    private static final String EMAIL = "bob@example.com";
    private static final User USER = new User();
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        USER.setId(2L);
        USER.setPassword("password123");
        USER.setEmail("bob@example.com");
        USER.setFirstName("Bob");
        USER.setLastName("Smith");
        Role admin = new Role();
        admin.setName(Role.RoleName.ROLE_ADMIN);
        USER.setRoles(Set.of(admin));

    }

    @Test
    @DisplayName("Get user by email")
    public void authenticate_validEmail_ReturnToken() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
        UserDetails actualOptional = customUserDetailsService.loadUserByUsername(EMAIL);
        User actual = (User) actualOptional;
        assertNotNull(actual);
        assertEquals(USER, actual);
    }

    @Test
    @DisplayName("Get user by email invalid email")
    public void authenticate_invalidEmail_throwException() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class,
                        () -> customUserDetailsService.loadUserByUsername(EMAIL));
        assertEquals("Can't find user by email", exception.getMessage());
    }
}
