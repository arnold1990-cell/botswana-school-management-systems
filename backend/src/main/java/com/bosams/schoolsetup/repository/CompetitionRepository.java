package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.Competition;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface CompetitionRepository extends JpaRepository<Competition, Long> { Page<Competition> findBySchoolId(Long schoolId, Pageable pageable); }
