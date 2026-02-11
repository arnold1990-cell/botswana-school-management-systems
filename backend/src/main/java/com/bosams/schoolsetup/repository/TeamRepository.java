package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Page<Team> findBySchoolId(Long schoolId, Pageable pageable);
}
