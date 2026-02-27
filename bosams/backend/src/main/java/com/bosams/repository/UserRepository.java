package com.bosams.repository;
import com.bosams.domain.*;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface UserRepository extends JpaRepository<UserEntity, Long> { Optional<UserEntity> findByEmail(String email); List<UserEntity> findByRole(Enums.Role role); }
