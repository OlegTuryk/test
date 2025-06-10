package com.chatbuddy.repository;

import com.chatbuddy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getByName(Role.RoleName roleName);
}
