package com.bosams.hr;

import com.bosams.hr.TrainingAttendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingAttendanceRepository extends JpaRepository<TrainingAttendance, Long> {

    Page<TrainingAttendance> findBySchoolIdAndTrainingProgramId(
            Long schoolId,
            Long programId,
            Pageable p
    );
}
