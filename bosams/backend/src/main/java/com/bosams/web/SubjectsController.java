package com.bosams.web;

import com.bosams.domain.Enums;
import com.bosams.domain.SubjectEntity;
import com.bosams.repository.SubjectRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectsController {
    private static final Logger log = LoggerFactory.getLogger(SubjectsController.class);

    private final SubjectRepository subjects;

    public SubjectsController(SubjectRepository subjects) {
        this.subjects = subjects;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<SubjectEntity> list(@RequestParam(required = false) Integer grade,
                                    @RequestParam(required = false) Enums.SchoolLevel level) {
        List<SubjectEntity> found = level != null
                ? subjects.findBySchoolLevelAndStatusOrderByNameAsc(level, Enums.EntityStatus.ACTIVE)
                : subjects.findByStatusOrderBySchoolLevelAscGradeFromAscNameAsc(Enums.EntityStatus.ACTIVE);
        if (grade != null) {
            found = found.stream().filter(s -> grade >= s.getGradeFrom() && grade <= s.getGradeTo()).toList();
        }
        log.info("Subjects lookup grade={} level={} count={}", grade, level, found.size());
        return found;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectEntity create(@Valid @RequestBody SubjectRequest request) {
        SubjectEntity s = new SubjectEntity();
        s.setName(request.name());
        s.setCode(request.code() == null ? request.name().toUpperCase().replaceAll("[^A-Z0-9]+", "_") : request.code());
        s.setSchoolLevel(request.level() == null ? Enums.SchoolLevel.PRIMARY : request.level());
        s.setGradeFrom(request.gradeFrom() == null ? 1 : request.gradeFrom());
        s.setGradeTo(request.gradeTo() == null ? 7 : request.gradeTo());
        s.setElective(Boolean.TRUE.equals(request.elective()));
        s.setStatus(Enums.EntityStatus.ACTIVE);
        return subjects.save(s);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectEntity update(@PathVariable Long id, @Valid @RequestBody SubjectRequest request) {
        SubjectEntity s = subjects.findById(id).orElseThrow();
        s.setName(request.name());
        s.setCode(request.code() == null ? s.getCode() : request.code());
        if (request.level() != null) s.setSchoolLevel(request.level());
        if (request.gradeFrom() != null) s.setGradeFrom(request.gradeFrom());
        if (request.gradeTo() != null) s.setGradeTo(request.gradeTo());
        if (request.elective() != null) s.setElective(request.elective());
        return subjects.save(s);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { subjects.deleteById(id); }

    public record SubjectRequest(@NotBlank String name, String code, Enums.SchoolLevel level, Integer gradeFrom, Integer gradeTo, Boolean elective) {}
}
