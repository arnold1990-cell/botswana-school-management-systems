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

    @PostMapping("/teacher/marks/submit")
    @PreAuthorize("hasRole('TEACHER')")
    public AcademicMarksService.StatusResponse submit(@AuthenticationPrincipal UserEntity user,
                                                      @RequestParam Long subjectId,
                                                      @RequestParam Long taskId,
                                                      @RequestParam Integer gradeLevel) {
        return service.submit(user.getId(), subjectId, taskId, gradeLevel);
    }

    @PostMapping("/admin/marks/unlock")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public AcademicMarksService.StatusResponse unlock(@AuthenticationPrincipal UserEntity user,
                                                      @RequestParam Long subjectId,
                                                      @RequestParam Long taskId,
                                                      @RequestParam Integer gradeLevel) {
        return service.unlock(user.getId(), subjectId, taskId, gradeLevel);
    }

    @GetMapping("/marks/status")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','PRINCIPAL')")
    public AcademicMarksService.StatusResponse status(@RequestParam Long subjectId,
                                                      @RequestParam Long taskId,
                                                      @RequestParam Integer gradeLevel) {
        return service.status(subjectId, taskId, gradeLevel);
    }

    @GetMapping("/reports/term")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','PRINCIPAL')")
    public List<AcademicMarksService.TermReportRow> report(@RequestParam Integer year, @RequestParam Integer termNumber, @RequestParam Integer gradeLevel, @RequestParam Long subjectId) {
        return service.termReport(year, termNumber, gradeLevel, subjectId);
    }
}
