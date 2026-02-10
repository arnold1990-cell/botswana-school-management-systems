package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.ClassRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {
    Page<ClassRoom> findBySchoolIdAndAcademicYearId(Long schoolId, Long academicYearId, Pageable pageable);

    boolean existsBySchoolIdAndAcademicYearIdAndCode(Long schoolId, Long academicYearId, String code);

    boolean existsBySchoolIdAndAcademicYearIdAndCodeAndIdNot(Long schoolId, Long academicYearId, String code, Long id);
}
