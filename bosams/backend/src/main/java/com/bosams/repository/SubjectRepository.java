package com.bosams.repository;

import com.bosams.domain.Enums;
import com.bosams.domain.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
    Optional<SubjectEntity> findByNameIgnoreCase(String name);

    List<SubjectEntity> findByStatusOrderBySchoolLevelAscGradeFromAscNameAsc(Enums.EntityStatus status);

    List<SubjectEntity> findBySchoolLevelAndStatusOrderByGradeFromAscNameAsc(Enums.SchoolLevel schoolLevel,
                                                                              Enums.EntityStatus status);

    List<SubjectEntity> findByStatusAndGradeFromLessThanEqualAndGradeToGreaterThanEqualOrderBySchoolLevelAscGradeFromAscNameAsc(
            Enums.EntityStatus status,
            Integer gradeFrom,
            Integer gradeTo
    );

    List<SubjectEntity> findBySchoolLevelAndStatusAndGradeFromLessThanEqualAndGradeToGreaterThanEqualOrderByGradeFromAscNameAsc(
            Enums.SchoolLevel schoolLevel,
            Enums.EntityStatus status,
            Integer gradeFrom,
            Integer gradeTo
    );
}
