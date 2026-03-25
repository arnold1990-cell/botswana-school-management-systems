package com.bosams.web;

import com.bosams.domain.Enums;
import com.bosams.service.SubjectsService;
import com.bosams.web.dto.SubjectDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectsController {
    private final SubjectsService subjectsService;

    public SubjectsController(SubjectsService subjectsService) {
        this.subjectsService = subjectsService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public List<SubjectDto.SubjectResponse> list(@RequestParam(required = false) Integer grade,
                                                 @RequestParam(required = false) Enums.SchoolLevel level) {
        return subjectsService.listSubjects(level, grade);
    }
}
