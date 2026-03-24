package com.bosams.web.dto;

import com.bosams.domain.Enums;
import com.bosams.domain.StudentEntity;

public record StudentDto(
        Long id,
        String admissionNo,
        String firstName,
        String lastName,
        Enums.Gender gender,
        Integer gradeLevel,
        Integer rollNumber,
        String studentCategory,
        String guardianName,
        String guardianPhone,
        String guardianEmail,
        Enums.EntityStatus status
) {
    public static StudentDto from(StudentEntity student) {
        return new StudentDto(
                student.getId(),
                student.getAdmissionNo(),
                student.getFirstName(),
                student.getLastName(),
                student.getGender(),
                student.getGradeLevel(),
                student.getRollNumber(),
                student.getStudentCategory(),
                student.getGuardianName(),
                student.getGuardianPhone(),
                student.getGuardianEmail(),
                student.getStatus()
        );
    }
}
