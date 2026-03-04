package com.bosams.service;

import com.bosams.domain.*;
import com.bosams.repository.*;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
public class AcademicsService {
    private static final Logger log = LoggerFactory.getLogger(AcademicsService.class);

    private final AcademicYearRepository academicYears;
    private final TermRepository terms;
    private final AssessmentTaskRepository tasks;

    public AcademicsService(AcademicYearRepository academicYears, TermRepository terms, AssessmentTaskRepository tasks) {
        this.academicYears = academicYears;
        this.terms = terms;
        this.tasks = tasks;
    }

    @Transactional
    public AcademicYear createAcademicYear(Integer year) {
        if (academicYears.findByYear(year).isPresent()) {
            throw new ValidationException("Academic year already exists");
        }
        AcademicYear ay = new AcademicYear();
        ay.setYear(year);
        ay.setLabel(String.valueOf(year));
        ay.setStartDate(LocalDate.of(year, Month.JANUARY, 1));
        ay.setEndDate(LocalDate.of(year, Month.DECEMBER, 31));
        ay.setActive(true);
        ay.setStatus(Enums.EntityStatus.ACTIVE);
        academicYears.findByActiveTrue().ifPresent(active -> {
            active.setActive(false);
            academicYears.save(active);
        });
        ay = academicYears.save(ay);
        createTermsAndTasks(ay);
        return ay;
    }

    @Transactional
    public AcademicYear ensureActiveYearSetup() {
        AcademicYear active = academicYears.findByActiveTrue()
                .orElseGet(() -> createAcademicYear(LocalDate.now().getYear()));

        if (terms.findByAcademicYearYearOrderByTermNo(active.getYear()).size() < 3) {
            log.info("Term seed incomplete for year={}, creating missing terms/tasks", active.getYear());
            ensureTermsAndTasks(active);
        }

        return active;
    }

    @Transactional
    public void createTermsAndTasks(AcademicYear year) {
        for (int i = 1; i <= 3; i++) {
            Term term = new Term();
            term.setAcademicYear(year);
            term.setTermNo(i);
            term.setStartDate(LocalDate.of(year.getYear(), ((i - 1) * 4) + 1, 1));
            term.setEndDate(term.getStartDate().plusMonths(4).minusDays(1));
            Term savedTerm = terms.save(term);
            for (Enums.AssessmentType type : List.of(Enums.AssessmentType.CAT, Enums.AssessmentType.EXAM)) {
                AssessmentTaskEntity task = new AssessmentTaskEntity();
                task.setTerm(savedTerm);
                task.setType(type);
                task.setMaxScore(50);
                tasks.save(task);
            }
        }
    }

    @Transactional
    public void ensureTermsAndTasks(AcademicYear year) {
        for (int i = 1; i <= 3; i++) {
            Term term = terms.findByAcademicYearYearAndTermNo(year.getYear(), i).orElseGet(() -> {
                Term newTerm = new Term();
                newTerm.setAcademicYear(year);
                newTerm.setTermNo(i);
                newTerm.setStartDate(LocalDate.of(year.getYear(), ((i - 1) * 4) + 1, 1));
                newTerm.setEndDate(newTerm.getStartDate().plusMonths(4).minusDays(1));
                return terms.save(newTerm);
            });

            for (Enums.AssessmentType type : List.of(Enums.AssessmentType.CAT, Enums.AssessmentType.EXAM)) {
                tasks.findByTermIdAndType(term.getId(), type).orElseGet(() -> {
                    AssessmentTaskEntity task = new AssessmentTaskEntity();
                    task.setTerm(term);
                    task.setType(type);
                    task.setMaxScore(50);
                    return tasks.save(task);
                });
            }
        }
    }
}
