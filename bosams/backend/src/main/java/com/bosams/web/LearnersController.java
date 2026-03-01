package com.bosams.web;

import com.bosams.domain.Enums;
import com.bosams.domain.StudentEntity;
import com.bosams.repository.StudentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learners")
public class LearnersController {
    private final StudentRepository learners;

    public LearnersController(StudentRepository learners) {
        this.learners = learners;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<StudentEntity> list(@RequestParam(required = false) Integer gradeLevel) {
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
}
