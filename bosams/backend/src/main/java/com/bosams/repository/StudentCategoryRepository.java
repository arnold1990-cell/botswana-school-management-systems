package com.bosams.repository;

import com.bosams.domain.Enums;
import com.bosams.domain.StudentCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentCategoryRepository extends JpaRepository<StudentCategoryEntity, Long> {
    Optional<StudentCategoryEntity> findByNameIgnoreCase(String name);
    List<StudentCategoryEntity> findByStatusOrderByNameAsc(Enums.EntityStatus status);
}
