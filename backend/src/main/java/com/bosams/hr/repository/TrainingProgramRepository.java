package com.bosams.hr.repository;

import com.bosams.hr.entity.TrainingProgram;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Long> {

    Page<TrainingProgram> findBySchoolIdAndStartDateBetween(
            Long schoolId,
            LocalDate from,
            LocalDate to,
            Pageable p
    );

    Page<TrainingProgram> findBySchoolId(Long schoolId, Pageable p);
}
