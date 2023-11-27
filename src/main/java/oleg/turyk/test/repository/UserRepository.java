package oleg.turyk.test.repository;

import java.util.Optional;
import oleg.turyk.test.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles "
            + "WHERE u.email = :email or "
            + "(:username IS NULL OR u.username = :username)")
    Optional<User> findByEmail(String email, String username);
}
