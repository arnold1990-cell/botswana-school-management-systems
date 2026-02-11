package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    Page<Competition> findBySchoolId(Long schoolId, Pageable pageable);
}
