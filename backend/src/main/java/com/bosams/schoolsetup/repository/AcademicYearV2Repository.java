package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.AcademicYear;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicYearV2Repository extends JpaRepository<AcademicYear, UUID> {
}
