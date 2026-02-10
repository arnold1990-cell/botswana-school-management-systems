package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.Grade;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeV2Repository extends JpaRepository<Grade, UUID> {
}
