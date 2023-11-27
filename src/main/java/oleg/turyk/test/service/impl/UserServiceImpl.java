package oleg.turyk.test.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import oleg.turyk.test.dto.user.UserInformationDto;
import oleg.turyk.test.dto.user.UserRegistrationRequestDto;
import oleg.turyk.test.exception.RegistrationException;
import oleg.turyk.test.mapper.UserMapper;
import oleg.turyk.test.model.Role;
import oleg.turyk.test.model.User;
import oleg.turyk.test.repository.RoleRepository;
import oleg.turyk.test.repository.UserRepository;
import oleg.turyk.test.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserInformationDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(userRegistrationRequestDto.getEmail(),
                userRegistrationRequestDto.getUsername()).isPresent()) {
            throw new RegistrationException("Unable to complete registration");
        }
        User user = userMapper.toModel(userRegistrationRequestDto);
        Role role = roleRepository.getByName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(role));
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDto.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }
}
