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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/learners")
public class LearnersController {
    private final StudentRepository learners;
    private final AuthorizationService auth;

    public LearnersController(StudentRepository learners, AuthorizationService auth) {
        this.learners = learners;
        this.auth = auth;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public StudentEntity create(@Valid @RequestBody CreateLearnerRequest req) {
        StudentEntity learner = new StudentEntity();
        learner.setAdmissionNo(req.admissionNo());
        learner.setFirstName(req.firstName());
        learner.setLastName(req.lastName());
        learner.setGender(req.gender());
        learner.setGradeLevel(req.gradeLevel());
        learner.setStatus(Enums.EntityStatus.ACTIVE);
        return learners.save(learner);
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
        return learners.save(learner);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<StudentEntity> list(@AuthenticationPrincipal UserEntity user, @RequestParam(required = false) Integer gradeLevel) {
        if (auth.isTeacher(user)) {
            Long activeYearId = auth.getActiveAcademicYear().getId();
            Set<Integer> assignedGrades = auth.teacherGradeLevels(user.getId(), activeYearId);
            if (assignedGrades.isEmpty()) {
                return List.of();
            }
            if (gradeLevel != null && !assignedGrades.contains(gradeLevel)) {
                return List.of();
            }
            return learners.findAll().stream()
                    .filter(l -> l.getGradeLevel() != null)
                    .filter(l -> gradeLevel == null ? assignedGrades.contains(l.getGradeLevel()) : gradeLevel.equals(l.getGradeLevel()))
                    .toList();
        }

        if (gradeLevel != null) {
            return learners.findByGradeLevel(gradeLevel);
        }
        return learners.findAll();
    }

    public record CreateLearnerRequest(
            @NotBlank String admissionNo,
            @NotBlank String firstName,
            @NotBlank String lastName,
            Enums.Gender gender,
            @Min(1) @Max(7) Integer gradeLevel
    ) {}

    public record UpdateLearnerRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            Enums.Gender gender,
            @Min(1) @Max(7) Integer gradeLevel
    ) {}
}
