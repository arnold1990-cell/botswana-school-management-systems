package com.bosams.web;

import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TeacherAssignmentController {
    private final UserRepository users;
    private final TeacherAssignmentRepository assignments;
    private final AcademicYearRepository years;
    private final StreamRepository streams;
    private final SubjectRepository subjects;
    private final PasswordEncoder encoder;

    public TeacherAssignmentController(UserRepository users, TeacherAssignmentRepository assignments, AcademicYearRepository years, StreamRepository streams, SubjectRepository subjects, PasswordEncoder encoder) {
        this.users = users;
        this.assignments = assignments;
        this.years = years;
        this.streams = streams;
        this.subjects = subjects;
        this.encoder = encoder;
    }

    @PostMapping("/admin/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public TeacherUserResponse createTeacher(@RequestBody CreateTeacherRequest req) {
        if (users.findByEmail(req.email()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "User with email already exists");
        }
        UserEntity user = new UserEntity();
        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setPasswordHash(encoder.encode(req.password()));
        user.setRole(Enums.Role.TEACHER);
        user.setStatus(Enums.UserStatus.ACTIVE);
        UserEntity saved = users.save(user);
        return new TeacherUserResponse(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole().name(), saved.getStatus().name());
    }

    @GetMapping("/admin/teachers")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public List<TeacherUserResponse> teachers() {
        return users.findByRole(Enums.Role.TEACHER)
                .stream()
                .map(u -> new TeacherUserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole().name(), u.getStatus().name()))
                .toList();
    }


    @GetMapping("/teachers")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public List<TeacherUserResponse> teachersLookup() {
        return teachers();
    }

    @PostMapping("/admin/teacher-assignments")
    @PreAuthorize("hasRole('ADMIN')")
    public TeacherAssignment create(@RequestBody AssignReq req) {
        TeacherAssignment ta = new TeacherAssignment();
        UserEntity teacher = users.findById(req.teacherUserId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Teacher not found"));
        if (teacher.getRole() != Enums.Role.TEACHER) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_TEACHER", "User must have TEACHER role");
        }
        ta.setTeacher(teacher);
        ta.setAcademicYear(years.findById(req.academicYearId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Academic year not found")));
        ta.setGradeLevel(req.gradeLevel());
        ta.setSubject(subjects.findById(req.subjectId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Subject not found")));
        if (req.streamId() != null) {
            ta.setStream(streams.findById(req.streamId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Stream not found")));
        }
        return assignments.save(ta);
    }

    @GetMapping("/admin/teacher-assignments")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public List<TeacherAssignment> list(@RequestParam(required = false) UUID teacherUserId,
                                        @RequestParam(required = false) Long academicYearId) {
        List<TeacherAssignment> all = assignments.findAll();
        return all.stream()
                .filter(a -> teacherUserId == null || a.getTeacher().getId().equals(teacherUserId))
                .filter(a -> academicYearId == null || a.getAcademicYear().getId().equals(academicYearId))
                .toList();
    }

    @GetMapping("/teacher/my-assignments")
    @PreAuthorize("hasRole('TEACHER')")
    public List<TeacherAssignment> myAssignments(@AuthenticationPrincipal UserEntity me,
                                                 @RequestParam(required = false) Long academicYearId) {
        Long year = academicYearId != null ? academicYearId : years.findByActiveTrue().orElseThrow().getId();
        return assignments.findByTeacherIdAndAcademicYearIdAndActiveTrue(me.getId(), year);
    }

    public record CreateTeacherRequest(@NotBlank String fullName, @NotBlank String email, @NotBlank String password) {}
    public record TeacherUserResponse(UUID id, String fullName, String email, String role, String status) {}
    public record AssignReq(UUID teacherUserId, Long academicYearId, @Min(1) @Max(7) Integer gradeLevel, Long subjectId, Long streamId) {}
}
