package com.bosams.repository;

import com.bosams.domain.Enums;
import com.bosams.domain.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
class UserRepositoryTest {
    @Autowired UserRepository users;

    @Test
    void findByEmailAndRoleWorks() {
        UserEntity user = new UserEntity();
        user.setFullName("Admin");
        user.setEmail("admin@bosams.test");
        user.setPasswordHash("hash");
        user.setRole(Enums.Role.ADMIN);
        user.setStatus(Enums.UserStatus.ACTIVE);
        users.save(user);

        assertThat(users.findByEmail("admin@bosams.test")).isPresent();
        assertThat(users.findByRole(Enums.Role.ADMIN)).hasSize(1);
    }
}
