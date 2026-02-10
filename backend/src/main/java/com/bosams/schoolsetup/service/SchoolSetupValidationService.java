package com.bosams.schoolsetup.service;

import com.bosams.common.DuplicateResourceException;
import com.bosams.schoolsetup.repository.ClassRoomRepository;
import com.bosams.schoolsetup.repository.DemeritCodeRepository;
import com.bosams.schoolsetup.repository.GradeRepository;
import com.bosams.schoolsetup.repository.HouseRepository;
import com.bosams.schoolsetup.repository.MeritCodeRepository;
import org.springframework.stereotype.Service;

@Service
public class SchoolSetupValidationService {
    private final GradeRepository grades;
    private final ClassRoomRepository classes;
    private final HouseRepository houses;
    private final MeritCodeRepository merits;
    private final DemeritCodeRepository demerits;

    public SchoolSetupValidationService(
            GradeRepository grades,
            ClassRoomRepository classes,
            HouseRepository houses,
            MeritCodeRepository merits,
            DemeritCodeRepository demerits) {
        this.grades = grades;
        this.classes = classes;
        this.houses = houses;
        this.merits = merits;
        this.demerits = demerits;
    }

    public void ensureUniqueGradeName(Long schoolId, String name, Long currentId) {
        var duplicate = currentId == null
                ? grades.existsBySchoolIdAndName(schoolId, name)
                : grades.existsBySchoolIdAndNameAndIdNot(schoolId, name, currentId);
        if (duplicate) {
            throw new DuplicateResourceException("Grade", "name", name, schoolId);
        }
    }

    public void ensureUniqueClassCode(Long schoolId, Long academicYearId, String code, Long currentId) {
        var duplicate = currentId == null
                ? classes.existsBySchoolIdAndAcademicYearIdAndCode(schoolId, academicYearId, code)
                : classes.existsBySchoolIdAndAcademicYearIdAndCodeAndIdNot(schoolId, academicYearId, code, currentId);
        if (duplicate) {
            throw new DuplicateResourceException("ClassRoom", "code", code, schoolId);
        }
    }

    public void ensureUniqueHouseName(Long schoolId, String name, Long currentId) {
        var duplicate = currentId == null
                ? houses.existsBySchoolIdAndName(schoolId, name)
                : houses.existsBySchoolIdAndNameAndIdNot(schoolId, name, currentId);
        if (duplicate) {
            throw new DuplicateResourceException("House", "name", name, schoolId);
        }
    }

    public void ensureUniqueMeritCode(Long schoolId, String code, Long currentId) {
        var duplicate = currentId == null
                ? merits.existsBySchoolIdAndCode(schoolId, code)
                : merits.existsBySchoolIdAndCodeAndIdNot(schoolId, code, currentId);
        if (duplicate) {
            throw new DuplicateResourceException("MeritCode", "code", code, schoolId);
        }
    }

    public void ensureUniqueDemeritCode(Long schoolId, String code, Long currentId) {
        var duplicate = currentId == null
                ? demerits.existsBySchoolIdAndCode(schoolId, code)
                : demerits.existsBySchoolIdAndCodeAndIdNot(schoolId, code, currentId);
        if (duplicate) {
            throw new DuplicateResourceException("DemeritCode", "code", code, schoolId);
        }
    }
}
