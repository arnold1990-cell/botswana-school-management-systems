package com.bosams.learnerparent.domain;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerRepository extends JpaRepository<Learner, Long> {
    Page<Learner> findBySchoolId(Long schoolId, Pageable p);

    Page<Learner> findBySchoolIdAndStatus(Long schoolId, LearnerStatus status, Pageable p);

    Page<Learner> findBySchoolIdAndCurrentAcademicYearIdAndCurrentGradeIdAndCurrentClassRoomId(Long s, Long y, Long g, Long c, Pageable p);

    Page<Learner> findBySchoolIdAndCurrentClassRoomId(Long schoolId, Long classRoomId, Pageable pageable);

    long countBySchoolIdAndCurrentAcademicYearIdAndCurrentGradeIdAndCurrentClassRoomId(Long s, Long y, Long g, Long c);
}
