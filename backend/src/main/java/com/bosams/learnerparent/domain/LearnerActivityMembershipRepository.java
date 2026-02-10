package com.bosams.learnerparent.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerActivityMembershipRepository extends JpaRepository<LearnerActivityMembership, Long> { Page<LearnerActivityMembership> findBySchoolIdAndLearnerId(Long schoolId, Long learnerId, Pageable p); Page<LearnerActivityMembership> findBySchoolIdAndActivityTypeAndActivityId(Long schoolId, ActivityType type, Long activityId, Pageable p); }
