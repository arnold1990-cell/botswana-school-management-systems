package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.Team;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface TeamRepository extends JpaRepository<Team, Long> { Page<Team> findBySchoolId(Long schoolId, Pageable pageable); }
