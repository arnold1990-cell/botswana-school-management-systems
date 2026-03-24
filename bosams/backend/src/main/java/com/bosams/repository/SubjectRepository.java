package com.bosams.repository;

import com.bosams.domain.Enums;
import com.bosams.domain.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
    Optional<SubjectEntity> findByNameIgnoreCase(String name);
    List<SubjectEntity> findBySchoolLevelAndStatusOrderByNameAsc(Enums.SchoolLevel schoolLevel, Enums.EntityStatus status);
    List<SubjectEntity> findByStatusOrderBySchoolLevelAscGradeFromAscNameAsc(Enums.EntityStatus status);
}
