package com.bosams.learnerparent.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerParentLinkRepository extends JpaRepository<LearnerParentLink, Long> { Page<LearnerParentLink> findBySchoolIdAndLearnerId(Long schoolId, Long learnerId, Pageable p); Page<LearnerParentLink> findBySchoolIdAndParentId(Long schoolId, Long parentId, Pageable p); boolean existsBySchoolIdAndLearnerIdAndIsPrimaryContactTrue(Long schoolId, Long learnerId); boolean existsBySchoolIdAndParentIdAndIsPrimaryContactTrue(Long schoolId, Long parentId); void deleteBySchoolIdAndLearnerIdAndParentId(Long schoolId, Long learnerId, Long parentId); Page<LearnerParentLink> findBySchoolIdAndIsPrimaryContactTrue(Long schoolId, Pageable p); }
