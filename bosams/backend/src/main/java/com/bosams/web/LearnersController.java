package com.bosams.web;

import com.bosams.common.ApiException;
import com.bosams.domain.Enums;
import com.bosams.domain.StudentEntity;
import com.bosams.domain.UserEntity;
import com.bosams.repository.StudentRepository;
import com.bosams.service.AuthorizationService;
import com.bosams.service.StudentManagementService;
import com.bosams.web.dto.StudentDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/learners")
public class LearnersController {
    private static final Logger log = LoggerFactory.getLogger(LearnersController.class);

    private final StudentRepository learners;
    private final AuthorizationService auth;
    private final StudentManagementService studentManagementService;

    public LearnersController(StudentRepository learners, AuthorizationService auth, StudentManagementService studentManagementService) {
        this.learners = learners;
        this.auth = auth;
        this.studentManagementService = studentManagementService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public StudentDto create(@AuthenticationPrincipal UserEntity user, @Valid @RequestBody CreateLearnerRequest req) {
        auth.enforceSchoolAccess(user, req.schoolId());
        StudentEntity learner = new StudentEntity();
        learner.setAdmissionNo(req.admissionNo());
        learner.setFirstName(req.firstName());
        learner.setLastName(req.lastName());
        learner.setGender(req.gender());
        learner.setGradeLevel(req.gradeLevel());
        learner.setStudentCategory(req.studentCategory());
        learner.setGuardianName(req.guardianName());
        learner.setGuardianPhone(req.guardianPhone());
        learner.setGuardianEmail(req.guardianEmail());
        learner.setStatus(Enums.EntityStatus.ACTIVE);

        StudentEntity saved = learners.save(learner);
        log.info("Learner created userId={} role={} learnerId={} admissionNo={} gradeLevel={} status={}",
                user.getId(), user.getRole(), saved.getId(), saved.getAdmissionNo(), saved.getGradeLevel(), saved.getStatus());
        return StudentDto.from(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public StudentDto update(@PathVariable Long id, @Valid @RequestBody UpdateLearnerRequest req) {
        StudentEntity learner = learners.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Learner not found"));
        learner.setFirstName(req.firstName());
        learner.setLastName(req.lastName());
        learner.setGender(req.gender());
        learner.setGradeLevel(req.gradeLevel());
        learner.setStudentCategory(req.studentCategory());
        learner.setGuardianName(req.guardianName());
        learner.setGuardianPhone(req.guardianPhone());
        learner.setGuardianEmail(req.guardianEmail());
        learner.setStatus(req.status() == null ? learner.getStatus() : req.status());
        return StudentDto.from(learners.save(learner));
    }

    @PatchMapping("/{id}/roll-number")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public StudentDto assignRollNumber(@PathVariable Long id, @Valid @RequestBody AssignRollNumberRequest req) {
        StudentEntity updated = studentManagementService.assignRollNumber(id, req.rollNumber());
        return StudentDto.from(updated);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public StudentDto updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest req) {
        StudentEntity learner = learners.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Learner not found"));
        learner.setStatus(req.status());
        return StudentDto.from(learners.save(learner));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<StudentDto> list(@AuthenticationPrincipal UserEntity user,
                                 @RequestParam(required = false) Integer gradeLevel,
                                 @RequestParam(required = false) Long streamId,
                                 @RequestParam(required = false) String query,
                                 @RequestParam(defaultValue = "true") boolean activeOnly) {
        auth.resolveSchoolId(user).ifPresent(schoolId -> auth.enforceSchoolAccess(user, schoolId));
        log.info("Learner list requested userId={} role={} gradeLevel={} streamId={} activeOnly={}", user.getId(), user.getRole(), gradeLevel, streamId, activeOnly);

        List<StudentEntity> base;
        if (streamId != null && gradeLevel != null) {
            base = learners.findByGradeLevelAndStreamId(gradeLevel, streamId);
        } else if (streamId != null) {
            base = learners.findByStreamId(streamId);
        } else {
            base = studentManagementService.list(gradeLevel, query, activeOnly);
        }

        Map<Integer, Long> gradeHistogram = base.stream()
                .filter(learner -> learner.getGradeLevel() != null)
                .collect(Collectors.groupingBy(StudentEntity::getGradeLevel, Collectors.counting()));

        if (auth.isTeacher(user)) {
            Long activeYearId = auth.getActiveAcademicYear().getId();
            Set<Integer> assignedGrades = auth.teacherGradeLevels(user.getId(), activeYearId);
            List<StudentDto> scoped = base.stream()
                    .filter(l -> l.getGradeLevel() != null && assignedGrades.contains(l.getGradeLevel()))
                    .map(StudentDto::from)
                    .toList();
            log.info("Learner list teacher scoped userId={} assignedGrades={} count={} gradeHistogram={}",
                    user.getId(), assignedGrades, scoped.size(), gradeHistogram);
            return scoped;
        }

        log.info("Learner list result count={} gradeHistogram={}", base.size(), gradeHistogram);
        return base.stream().map(StudentDto::from).toList();
    }

    public record CreateLearnerRequest(
            @NotBlank String admissionNo,
            @NotBlank String firstName,
            @NotBlank String lastName,
            Enums.Gender gender,
            @Min(1) @Max(7) Integer gradeLevel,
            @Pattern(regexp = "^[A-Za-z0-9 .,'-]{2,80}$", message = "Invalid student category") String studentCategory,
            @Pattern(regexp = "^[A-Za-z .'-]{2,100}$", message = "Invalid guardian name") String guardianName,
            @Pattern(regexp = "^[0-9+ -]{7,20}$", message = "Invalid guardian phone") String guardianPhone,
            String guardianEmail,
            Long schoolId
    ) {}

    public record UpdateLearnerRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            Enums.Gender gender,
            @Min(1) @Max(7) Integer gradeLevel,
            @Pattern(regexp = "^[A-Za-z0-9 .,'-]{2,80}$", message = "Invalid student category") String studentCategory,
            @Pattern(regexp = "^[A-Za-z .'-]{2,100}$", message = "Invalid guardian name") String guardianName,
            @Pattern(regexp = "^[0-9+ -]{7,20}$", message = "Invalid guardian phone") String guardianPhone,
            String guardianEmail,
            Enums.EntityStatus status
    ) {}

    public record AssignRollNumberRequest(@Min(1) @Max(99) Integer rollNumber) {}
    public record UpdateStatusRequest(@NotNull Enums.EntityStatus status) {}
}
