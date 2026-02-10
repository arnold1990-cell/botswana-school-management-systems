package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.ExtraMural;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtraMuralRepository extends JpaRepository<ExtraMural, UUID> {
}
