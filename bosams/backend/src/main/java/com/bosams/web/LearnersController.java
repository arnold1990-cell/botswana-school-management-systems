package com.bosams.web;

import com.bosams.common.ApiException;
import com.bosams.domain.Enums;
import com.bosams.domain.StudentEntity;
import com.bosams.domain.UserEntity;
import com.bosams.repository.StudentRepository;
import com.bosams.service.AuthorizationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/learners")
public class LearnersController {
    private static final Logger log = LoggerFactory.getLogger(LearnersController.class);

    private final StudentRepository learners;
    private final AuthorizationService auth;

    public LearnersController(StudentRepository learners, AuthorizationService auth) {
        this.learners = learners;
        this.auth = auth;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public StudentEntity create(@AuthenticationPrincipal UserEntity user, @Valid @RequestBody CreateLearnerRequest req) {
        auth.enforceSchoolAccess(user, req.schoolId());
        StudentEntity learner = new StudentEntity();
        learner.setAdmissionNo(req.admissionNo());
        learner.setFirstName(req.firstName());
        learner.setLastName(req.lastName());
        learner.setGender(req.gender());
        learner.setGradeLevel(req.gradeLevel());
        learner.setStatus(Enums.EntityStatus.ACTIVE);

        StudentEntity saved = learners.save(learner);
        log.info("Learner created userId={} role={} learnerId={} admissionNo={} gradeLevel={}", user.getId(), user.getRole(), saved.getId(), saved.getAdmissionNo(), saved.getGradeLevel());
        return saved;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public StudentEntity update(@PathVariable Long id, @Valid @RequestBody UpdateLearnerRequest req) {
        StudentEntity learner = learners.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Learner not found"));
        learner.setFirstName(req.firstName());
        learner.setLastName(req.lastName());
        learner.setGender(req.gender());
        learner.setGradeLevel(req.gradeLevel());
        learner.setStatus(req.status() == null ? learner.getStatus() : req.status());
        return learners.save(learner);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<StudentEntity> list(@AuthenticationPrincipal UserEntity user,
                                    @RequestParam(required = false) Integer gradeLevel,
                                    @RequestParam(required = false) Long streamId,
                                    @RequestParam(defaultValue = "true") boolean activeOnly) {
        auth.resolveSchoolId(user).ifPresent(schoolId -> auth.enforceSchoolAccess(user, schoolId));
        log.info("Learner list requested userId={} role={} gradeLevel={} streamId={} activeOnly={}", user.getId(), user.getRole(), gradeLevel, streamId, activeOnly);

        List<StudentEntity> base;
        if (streamId != null && gradeLevel != null) {
            base = learners.findByGradeLevelAndStreamId(gradeLevel, streamId);
        } else if (streamId != null) {
            base = learners.findByStreamId(streamId);
        } else if (gradeLevel != null) {
            base = activeOnly ? learners.findByGradeLevelAndStatus(gradeLevel, Enums.EntityStatus.ACTIVE) : learners.findByGradeLevel(gradeLevel);
        } else {
            base = activeOnly ? learners.findByStatus(Enums.EntityStatus.ACTIVE) : learners.findAll();
        }

        if (auth.isTeacher(user)) {
            Long activeYearId = auth.getActiveAcademicYear().getId();
            Set<Integer> assignedGrades = auth.teacherGradeLevels(user.getId(), activeYearId);
            List<StudentEntity> scoped = base.stream()
                    .filter(l -> l.getGradeLevel() != null && assignedGrades.contains(l.getGradeLevel()))
                    .toList();
            log.info("Learner list teacher scoped userId={} assignedGrades={} count={}", user.getId(), assignedGrades, scoped.size());
            return scoped;
        }

        log.info("Learner list result count={}", base.size());
        return base;
    }

    public record CreateLearnerRequest(
            @NotBlank String admissionNo,
            @NotBlank String firstName,
            @NotBlank String lastName,
            Enums.Gender gender,
            @Min(1) @Max(7) Integer gradeLevel,
            Long schoolId
    ) {}

    public record UpdateLearnerRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            Enums.Gender gender,
            @Min(1) @Max(7) Integer gradeLevel,
            Enums.EntityStatus status
    ) {}
}
