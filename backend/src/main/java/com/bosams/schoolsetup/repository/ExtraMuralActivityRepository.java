package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.ExtraMuralActivity;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface ExtraMuralActivityRepository extends JpaRepository<ExtraMuralActivity, Long> { Page<ExtraMuralActivity> findBySchoolId(Long schoolId, Pageable pageable); }
