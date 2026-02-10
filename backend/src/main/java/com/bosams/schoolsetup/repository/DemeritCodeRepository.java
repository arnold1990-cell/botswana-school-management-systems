package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.DemeritCode;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface DemeritCodeRepository extends JpaRepository<DemeritCode, Long> { Page<DemeritCode> findBySchoolId(Long schoolId, Pageable pageable); }
