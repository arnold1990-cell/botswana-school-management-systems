package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Page<Grade> findBySchoolId(Long schoolId, Pageable pageable);

    boolean existsBySchoolIdAndName(Long schoolId, String name);

    boolean existsBySchoolIdAndNameAndIdNot(Long schoolId, String name, Long id);
}
