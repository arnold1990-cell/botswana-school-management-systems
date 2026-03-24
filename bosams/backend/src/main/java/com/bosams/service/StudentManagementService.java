package com.bosams.service;

import com.bosams.common.ApiException;
import com.bosams.domain.Enums;
import com.bosams.domain.StudentEntity;
import com.bosams.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class StudentManagementService {
    private final StudentRepository students;

    public StudentManagementService(StudentRepository students) {
        this.students = students;
    }

    @Transactional(readOnly = true)
    public List<StudentEntity> list(Integer gradeLevel, String query, boolean activeOnly) {
        List<StudentEntity> base = gradeLevel == null
                ? (activeOnly ? students.findByStatus(Enums.EntityStatus.ACTIVE) : students.findAll())
                : (activeOnly ? students.findByGradeLevelAndStatus(gradeLevel, Enums.EntityStatus.ACTIVE) : students.findByGradeLevel(gradeLevel));

        if (query == null || query.isBlank()) {
            return base.stream()
                    .sorted(Comparator.comparing(StudentEntity::getGradeLevel, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(StudentEntity::getRollNumber, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(StudentEntity::getAdmissionNo))
                    .toList();
        }

        String search = query.trim().toLowerCase();
        return base.stream()
                .filter(student -> contains(student.getAdmissionNo(), search)
                        || contains(student.getFirstName(), search)
                        || contains(student.getLastName(), search)
                        || contains(student.getGuardianName(), search)
                        || contains(student.getStudentCategory(), search))
                .sorted(Comparator.comparing(StudentEntity::getGradeLevel, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(StudentEntity::getRollNumber, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(StudentEntity::getAdmissionNo))
                .toList();
    }

    @Transactional
    public StudentEntity assignRollNumber(Long studentId, Integer rollNumber) {
        StudentEntity student = students.findById(studentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Student not found"));

        if (student.getGradeLevel() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Student grade is required before assigning roll number");
        }

        boolean duplicate = students.findByGradeLevelOrderByRollNumberAsc(student.getGradeLevel()).stream()
                .anyMatch(s -> !s.getId().equals(studentId) && rollNumber.equals(s.getRollNumber()));
        if (duplicate) {
            throw new ApiException(HttpStatus.CONFLICT, "ROLL_NUMBER_IN_USE", "Roll number already assigned in this grade");
        }

        student.setRollNumber(rollNumber);
        return students.save(student);
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }
}
