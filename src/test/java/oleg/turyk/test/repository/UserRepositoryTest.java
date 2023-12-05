package oleg.turyk.test.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;
import oleg.turyk.test.model.Role;
import oleg.turyk.test.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/insert/add-user.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/delete/delete-user.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserRepositoryTest {
    private static final User USER = new User();
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        USER.setPassword("$2a$10$X15B5JIfbDItKTstPrQeAuFZTBP2gRFSZ80C3vVmWOSm065s.nIqG");
        USER.setEmail("bob@example.com");
        USER.setFirstName("Bob");
        USER.setLastName("Smith");
        Role admin = new Role();
        admin.setId(1L);
        admin.setName(Role.RoleName.ROLE_ADMIN);
        USER.setRoles(Set.of(admin));

    }

    @Test
    @DisplayName("Find user by email id")
    public void findByEmail_ValidEmail_ReturnUser() {
        Optional<User> actualOptional = userRepository.findByEmail(USER.getEmail());
        assertTrue(actualOptional.isPresent());
        User actual = actualOptional.get();
        assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(USER);
    }
}
