package com.bosams.service;

import com.bosams.common.ApiException;
import com.bosams.domain.AttendanceRecordEntity;
import com.bosams.domain.Enums;
import com.bosams.domain.StudentEntity;
import com.bosams.domain.UserEntity;
import com.bosams.repository.AttendanceRecordRepository;
import com.bosams.repository.StudentRepository;
import com.bosams.repository.TeacherAssignmentRepository;
import com.bosams.web.dto.AttendanceDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRecordRepository attendanceRecords;
    private final StudentRepository students;
    private final TeacherAssignmentRepository assignments;

    public AttendanceService(AttendanceRecordRepository attendanceRecords, StudentRepository students, TeacherAssignmentRepository assignments) {
        this.attendanceRecords = attendanceRecords;
        this.students = students;
        this.assignments = assignments;
    }

    @Transactional
    public List<AttendanceDto.AttendanceResponse> markAttendance(UserEntity actor, AttendanceDto.AttendanceMarkRequest request) {
        var attendanceDate = request.attendanceDate() == null ? LocalDate.now() : request.attendanceDate();
        if (actor.getRole() == Enums.Role.TEACHER && assignments.findByTeacherUserId(actor.getId()).stream().noneMatch(a -> a.getGradeLevel().equals(request.gradeLevel()) && a.isActive())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_ASSIGNED", "Teacher is not assigned to the selected grade");
        }

        return request.items().stream().map(item -> {
            StudentEntity student = students.findById(item.studentId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Student not found: " + item.studentId()));

            if (student.getGradeLevel() == null || !student.getGradeLevel().equals(request.gradeLevel())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "GRADE_MISMATCH", "Student %s is not in grade %s".formatted(student.getAdmissionNo(), request.gradeLevel()));
            }

            AttendanceRecordEntity record = attendanceRecords.findByAttendanceDateAndStudentId(attendanceDate, student.getId())
                    .orElseGet(AttendanceRecordEntity::new);
            record.setAttendanceDate(attendanceDate);
            record.setStudent(student);
            record.setStatus(item.status());
            record.setRemark(item.remark());
            record.setMarkedByUserId(actor.getId());

            return toResponse(attendanceRecords.save(record));
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceDto.AttendanceResponse> gradeAttendance(Integer gradeLevel, LocalDate attendanceDate) {
        LocalDate safeDate = attendanceDate == null ? LocalDate.now() : attendanceDate;
        return attendanceRecords.findByAttendanceDateAndStudentGradeLevelOrderByStudentFirstNameAsc(safeDate, gradeLevel)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceDto.AttendanceResponse> studentAttendance(Long studentId) {
        return attendanceRecords.findByStudentIdOrderByAttendanceDateDesc(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    private AttendanceDto.AttendanceResponse toResponse(AttendanceRecordEntity record) {
        var student = record.getStudent();
        return new AttendanceDto.AttendanceResponse(
                record.getId(),
                record.getAttendanceDate(),
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                student.getGradeLevel(),
                record.getStatus(),
                record.getRemark()
        );
    }
}
