package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.CompetitionEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionEventRepository extends JpaRepository<CompetitionEvent, UUID> {
}
