package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.ClassRoom;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> { Page<ClassRoom> findBySchoolIdAndAcademicYearId(Long schoolId, Long academicYearId, Pageable pageable); }
