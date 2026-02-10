package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.MeritCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeritCodeRepository extends JpaRepository<MeritCode, Long> {
    Page<MeritCode> findBySchoolId(Long schoolId, Pageable pageable);

    boolean existsBySchoolIdAndCode(Long schoolId, String code);

    boolean existsBySchoolIdAndCodeAndIdNot(Long schoolId, String code, Long id);
}
