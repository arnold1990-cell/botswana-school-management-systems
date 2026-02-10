package com.bosams.learnerparent.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentGuardianRepository extends JpaRepository<ParentGuardian, Long> { Page<ParentGuardian> findBySchoolIdAndStatus(Long schoolId, ParentStatus status, Pageable p); }
