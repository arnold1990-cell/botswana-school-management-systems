package com.bosams.web;

import com.bosams.domain.AcademicYear;
import com.bosams.domain.AssessmentTaskEntity;
import com.bosams.domain.Enums;
import com.bosams.domain.Term;
import com.bosams.repository.AcademicYearRepository;
import com.bosams.repository.AssessmentTaskRepository;
import com.bosams.repository.SubjectRepository;
import com.bosams.repository.TermRepository;
import com.bosams.service.AcademicsService;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AcademicsController {
    private static final Logger log = LoggerFactory.getLogger(AcademicsController.class);

    private final AcademicsService academicsService;
    private final AcademicYearRepository years;
    private final TermRepository terms;
    private final AssessmentTaskRepository tasks;
    private final SubjectRepository subjects;

    public AcademicsController(AcademicsService academicsService, AcademicYearRepository years, TermRepository terms, AssessmentTaskRepository tasks, SubjectRepository subjects) {
        this.academicsService = academicsService;
        this.years = years;
        this.terms = terms;
        this.tasks = tasks;
        this.subjects = subjects;
    }

    @PostMapping("/academic-years")
    @PreAuthorize("hasRole('ADMIN')")
    public AcademicYear create(@RequestBody CreateAcademicYearRequest request) {
        return academicsService.createAcademicYear(request.year());
    }

    @GetMapping("/academic-years/current")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public AcademicYear current() {
        return academicsService.ensureActiveYearSetup();
    }

    @GetMapping("/academics/active-year")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public AcademicYear activeYear() {
        return academicsService.ensureActiveYearSetup();
    }

    @GetMapping("/terms")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<Term> terms(@RequestParam(required = false) Integer year) {
        AcademicYear targetYear = year == null
                ? academicsService.ensureActiveYearSetup()
                : years.findByYear(year).orElseGet(() -> academicsService.createAcademicYear(year));

        academicsService.ensureTermsAndTasks(targetYear);
        List<Term> termList = terms.findByAcademicYearYearOrderByTermNo(targetYear.getYear());
        log.info("Terms lookup year={} count={}", targetYear.getYear(), termList.size());
        return termList;
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<AssessmentTaskEntity> tasks(@RequestParam Long termId,
                                            @RequestParam(required = false) Integer gradeLevel,
                                            @RequestParam(required = false) Long subjectId) {
        List<AssessmentTaskEntity> found = (gradeLevel != null && subjectId != null)
                ? tasks.findByTermIdAndGradeLevelAndSubjectIdOrderByTypeAscTitleAsc(termId, gradeLevel, subjectId)
                : tasks.findByTermIdOrderByType(termId);
        if (found.isEmpty() && gradeLevel != null && subjectId != null) {
            found = tasks.findByTermIdOrderByType(termId);
        }
        log.info("Tasks lookup termId={} count={}", termId, found.size());
        return found;
    }

    @PostMapping("/tasks")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public AssessmentTaskEntity createTask(@RequestBody CreateTaskRequest request) {
        Term term = terms.findById(request.termId()).orElseThrow();
        AssessmentTaskEntity task = new AssessmentTaskEntity();
        task.setTerm(term);
        task.setType(request.type());
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setGradeLevel(request.gradeLevel());
        task.setDueDate(request.dueDate());
        task.setMaxScore(request.maxScore() == null ? 50 : request.maxScore());
        if (request.subjectId() != null) {
            task.setSubject(subjects.findById(request.subjectId()).orElseThrow());
        }
        return tasks.save(task);
    }

    @GetMapping("/test-types")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<String> testTypes() {
        return Arrays.stream(Enums.AssessmentType.values()).map(Enum::name).toList();
    }

    public record CreateAcademicYearRequest(@NotNull Integer year) {}
    public record CreateTaskRequest(Long termId, Long subjectId, Integer gradeLevel, Enums.AssessmentType type, String title, String description, java.time.LocalDate dueDate, Integer maxScore) {}
}
