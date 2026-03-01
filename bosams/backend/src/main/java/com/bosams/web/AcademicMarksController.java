package com.bosams.web;

import com.bosams.domain.MarkEntryEntity;
import com.bosams.domain.UserEntity;
import com.bosams.service.AcademicMarksService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AcademicMarksController {
    private final AcademicMarksService service;

    public AcademicMarksController(AcademicMarksService service) {
        this.service = service;
    }

    @PostMapping("/marks/bulk")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','PRINCIPAL')")
    public List<MarkEntryEntity> bulk(@AuthenticationPrincipal UserEntity user, @RequestBody AcademicMarksService.BulkMarkRequest request) {
        return service.bulkSave(user.getId(), request);
    }

    @GetMapping("/reports/term")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','PRINCIPAL')")
    public List<AcademicMarksService.TermReportRow> report(@RequestParam Integer year, @RequestParam Integer termNumber, @RequestParam Integer gradeLevel, @RequestParam Long subjectId) {
        return service.termReport(year, termNumber, gradeLevel, subjectId);
    }
}
