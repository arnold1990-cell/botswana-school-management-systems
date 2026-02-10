package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.MeritCode;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeritCodeRepository extends JpaRepository<MeritCode, UUID> {
}
