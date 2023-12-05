package oleg.turyk.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import oleg.turyk.test.dto.user.UserRegistrationRequestDto;
import oleg.turyk.test.dto.user.UserRegistrationResponseDto;
import oleg.turyk.test.exception.RegistrationException;
import oleg.turyk.test.mapper.UserMapper;
import oleg.turyk.test.model.Role;
import oleg.turyk.test.model.User;
import oleg.turyk.test.repository.RoleRepository;
import oleg.turyk.test.repository.UserRepository;
import oleg.turyk.test.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final Long VALID_ID = 1L;

    private static final User USER = new User();
    private static final Role ROLE = new Role();
    private static final UserRegistrationResponseDto USER_REGISTRATION_RESPONSE_DTO
            = new UserRegistrationResponseDto(VALID_ID,
            USER.getEmail(), USER.getFirstName(), USER.getLastName());
    private static final UserRegistrationRequestDto USER_REGISTRATION_REQUEST_DTO
            = new UserRegistrationRequestDto();
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        USER.setId(VALID_ID);
        USER.setPassword("password123");
        USER.setEmail("bob@example.com");
        USER.setFirstName("Bob");
        USER.setLastName("Smith");
        ROLE.setName(Role.RoleName.ROLE_ADMIN);
        USER.setRoles(Set.of(ROLE));

        USER_REGISTRATION_REQUEST_DTO.setEmail(USER.getEmail());
        USER_REGISTRATION_REQUEST_DTO.setFirstName(USER.getFirstName());
        USER_REGISTRATION_REQUEST_DTO.setLastName(USER.getLastName());
        USER_REGISTRATION_REQUEST_DTO.setPassword(USER.getPassword());
        USER_REGISTRATION_REQUEST_DTO.setRepeatPassword(USER.getPassword());
    }

    @Test
    @DisplayName("Delete user by id")
    public void deleteById_UserExists_DeleteSuccessfully() {
        doNothing().when(userRepository).deleteById(VALID_ID);
        userService.deleteUser(VALID_ID);

        verify(userRepository).deleteById(VALID_ID);
    }

    @Test
    @DisplayName("Find all users")
    public void findAll_ReturnAllUser() {
        Pageable pageable = PageRequest.of(1, 2);
        Page<User> usersPage =
                new PageImpl<>(List.of(USER), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(usersPage);
        when(userMapper.toUserResponse(USER)).thenReturn(USER_REGISTRATION_RESPONSE_DTO);
        List<UserRegistrationResponseDto> actual = userService.getUsers(pageable);

        assertEquals(List.of(USER_REGISTRATION_RESPONSE_DTO), actual);
    }

    @Test
    @DisplayName("Register user")
    public void register_validUserRegistrationRequestDto_createUser() throws RegistrationException {
        when(userRepository.findByEmail(USER_REGISTRATION_REQUEST_DTO
                .getEmail())).thenReturn(Optional.empty());
        when(userMapper.toModel(USER_REGISTRATION_REQUEST_DTO)).thenReturn(USER);
        when(roleRepository.getByName(Role.RoleName.ROLE_ADMIN)).thenReturn(ROLE);
        when(passwordEncoder.encode(USER_REGISTRATION_REQUEST_DTO
                .getPassword())).thenReturn(USER.getPassword());
        when(userMapper.toUserResponse(USER)).thenReturn(USER_REGISTRATION_RESPONSE_DTO);
        when(userRepository.save(USER)).thenReturn(USER);

        UserRegistrationResponseDto actual = userService.register(USER_REGISTRATION_REQUEST_DTO);

        assertEquals(actual, USER_REGISTRATION_RESPONSE_DTO);
    }

    @Test
    @DisplayName("Register user which exist")
    public void register_existUser_throwException() {
        when(userRepository.findByEmail(USER_REGISTRATION_REQUEST_DTO
                .getEmail())).thenReturn(Optional.of(USER));
        RegistrationException registrationException = assertThrows(RegistrationException.class,
                () -> userService.register(USER_REGISTRATION_REQUEST_DTO));
        assertEquals("Unable to complete registration", registrationException.getMessage());
    }
}
