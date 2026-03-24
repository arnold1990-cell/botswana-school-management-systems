package com.bosams.web;

import com.bosams.domain.UserEntity;
import com.bosams.service.AnnouncementService;
import com.bosams.web.dto.AnnouncementDto;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER','STUDENT','PARENT','ACCOUNTANT')")
    public List<AnnouncementDto.AnnouncementResponse> visible(
            @AuthenticationPrincipal UserEntity actor,
            @RequestParam(required = false) Integer gradeLevel
    ) {
        return announcementService.visibleTo(actor.getRole(), gradeLevel);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public List<AnnouncementDto.AnnouncementResponse> all() {
        return announcementService.all();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public AnnouncementDto.AnnouncementResponse create(
            @AuthenticationPrincipal UserEntity actor,
            @Valid @RequestBody AnnouncementDto.AnnouncementRequest request
    ) {
        return announcementService.create(actor, request);
    }
}
