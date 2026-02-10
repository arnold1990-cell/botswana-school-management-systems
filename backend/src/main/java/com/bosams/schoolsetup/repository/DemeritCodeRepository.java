package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.DemeritCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemeritCodeRepository extends JpaRepository<DemeritCode, Long> {
    Page<DemeritCode> findBySchoolId(Long schoolId, Pageable pageable);

    boolean existsBySchoolIdAndCode(Long schoolId, String code);

    boolean existsBySchoolIdAndCodeAndIdNot(Long schoolId, String code, Long id);
}
