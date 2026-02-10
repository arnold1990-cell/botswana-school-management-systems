package com.bosams.learnerparent.domain;

import com.bosams.learnerparent.domain.enums.*;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerLeadershipRoleRepository extends JpaRepository<LearnerLeadershipRole, Long> { Page<LearnerLeadershipRole> findBySchoolIdAndLearnerId(Long schoolId, Long learnerId, Pageable p); Page<LearnerLeadershipRole> findBySchoolIdAndRoleTypeAndAcademicYearId(Long schoolId, LeadershipRoleType roleType, Long yearId, Pageable p); }
