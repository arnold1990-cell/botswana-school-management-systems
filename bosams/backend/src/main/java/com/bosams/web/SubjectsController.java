package com.bosams.web;

import com.bosams.domain.Enums;
import com.bosams.domain.SubjectEntity;
import com.bosams.repository.SubjectRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectsController {
    private final SubjectRepository subjects;

    public SubjectsController(SubjectRepository subjects) {
        this.subjects = subjects;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<SubjectEntity> list() { return subjects.findAll(); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectEntity create(@Valid @RequestBody SubjectRequest request) {
        SubjectEntity s = new SubjectEntity();
        s.setName(request.name());
        s.setCode(request.code());
        s.setStatus(Enums.EntityStatus.ACTIVE);
        return subjects.save(s);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SubjectEntity update(@PathVariable Long id, @Valid @RequestBody SubjectRequest request) {
        SubjectEntity s = subjects.findById(id).orElseThrow();
        s.setName(request.name());
        s.setCode(request.code());
        return subjects.save(s);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { subjects.deleteById(id); }

    public record SubjectRequest(@NotBlank String name, String code) {}
}
