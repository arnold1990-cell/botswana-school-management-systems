package com.bosams.web;

import com.bosams.domain.AcademicYear;
import com.bosams.domain.AssessmentTaskEntity;
import com.bosams.domain.Term;
import com.bosams.repository.AcademicYearRepository;
import com.bosams.repository.AssessmentTaskRepository;
import com.bosams.repository.TermRepository;
import com.bosams.service.AcademicsService;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AcademicsController {
    private final AcademicsService academicsService;
    private final AcademicYearRepository years;
    private final TermRepository terms;
    private final AssessmentTaskRepository tasks;

    public AcademicsController(AcademicsService academicsService, AcademicYearRepository years, TermRepository terms, AssessmentTaskRepository tasks) {
        this.academicsService = academicsService;
        this.years = years;
        this.terms = terms;
        this.tasks = tasks;
    }

    @PostMapping("/academic-years")
    @PreAuthorize("hasRole('ADMIN')")
    public AcademicYear create(@RequestBody CreateAcademicYearRequest request) {
        return academicsService.createAcademicYear(request.year());
    }

    @GetMapping("/academic-years/current")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public AcademicYear current() { return years.findByActiveTrue().orElseThrow(); }

    @GetMapping("/terms")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<Term> terms(@RequestParam Integer year) { return terms.findByAcademicYearYearOrderByTermNo(year); }

    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<AssessmentTaskEntity> tasks(@RequestParam Long termId) { return tasks.findByTermIdOrderByType(termId); }

    public record CreateAcademicYearRequest(@NotNull Integer year) {}
}
