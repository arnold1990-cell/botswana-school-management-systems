package com.bosams.repository;

import com.bosams.domain.PasswordResetRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequestEntity, Long> {
    List<PasswordResetRequestEntity> findAllByOrderByCreatedAtDesc();
}
