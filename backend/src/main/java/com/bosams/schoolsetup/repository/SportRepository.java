package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.Sport;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface SportRepository extends JpaRepository<Sport, Long> { Page<Sport> findBySchoolId(Long schoolId, Pageable pageable); }
