package com.bosams.hr.repository;

import com.bosams.hr.entity.EducatorSubjectExperience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducatorSubjectExperienceRepository
        extends JpaRepository<EducatorSubjectExperience, Long> {

    Page<EducatorSubjectExperience> findBySchoolIdAndStaffId(
            Long schoolId,
            Long staffId,
            Pageable p
    );
}
