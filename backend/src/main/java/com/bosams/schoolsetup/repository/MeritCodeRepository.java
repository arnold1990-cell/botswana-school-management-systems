package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.MeritCode;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface MeritCodeRepository extends JpaRepository<MeritCode, Long> { Page<MeritCode> findBySchoolId(Long schoolId, Pageable pageable); }
