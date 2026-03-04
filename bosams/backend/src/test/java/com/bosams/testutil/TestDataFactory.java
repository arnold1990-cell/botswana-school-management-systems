package com.bosams.testutil;

import com.bosams.domain.*;

import java.time.LocalDate;
import java.util.UUID;

public final class TestDataFactory {
    private TestDataFactory() {}

    public static UserEntity user(UUID id, Enums.Role role) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setEmail(role.name().toLowerCase() + "@bosams.test");
        user.setFullName(role.name() + " User");
        user.setPasswordHash("hash");
        user.setRole(role);
        user.setStatus(Enums.UserStatus.ACTIVE);
        return user;
    }

    public static AcademicYear academicYear(Long id, int year, boolean active) {
        AcademicYear academicYear = new AcademicYear();
        academicYear.setId(id);
        academicYear.setYear(year);
        academicYear.setLabel(String.valueOf(year));
        academicYear.setStartDate(LocalDate.of(year, 1, 1));
        academicYear.setEndDate(LocalDate.of(year, 12, 31));
        academicYear.setActive(active);
        academicYear.setStatus(Enums.EntityStatus.ACTIVE);
        return academicYear;
    }

    public static Term term(Long id, AcademicYear year, int termNo) {
        Term term = new Term();
        term.setId(id);
        term.setAcademicYear(year);
        term.setTermNo(termNo);
        term.setStartDate(LocalDate.of(year.getYear(), ((termNo - 1) * 4) + 1, 1));
        term.setEndDate(term.getStartDate().plusMonths(4).minusDays(1));
        return term;
    }

    public static SubjectEntity subject(Long id, String name) {
        SubjectEntity subject = new SubjectEntity();
        subject.setId(id);
        subject.setName(name);
        subject.setCode(name.substring(0, Math.min(3, name.length())).toUpperCase());
        subject.setStatus(Enums.EntityStatus.ACTIVE);
        return subject;
    }

    public static StudentEntity learner(Long id, int gradeLevel) {
        StudentEntity learner = new StudentEntity();
        learner.setId(id);
        learner.setAdmissionNo("ADM" + id);
        learner.setFirstName("First" + id);
        learner.setLastName("Last" + id);
        learner.setGradeLevel(gradeLevel);
        learner.setStatus(Enums.EntityStatus.ACTIVE);
        return learner;
    }
}
