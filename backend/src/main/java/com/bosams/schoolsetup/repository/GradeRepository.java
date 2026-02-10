package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.Grade;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface GradeRepository extends JpaRepository<Grade, Long> { Page<Grade> findBySchoolId(Long schoolId, Pageable pageable); }
