package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.DemeritCode;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemeritCodeRepository extends JpaRepository<DemeritCode, UUID> {
}
