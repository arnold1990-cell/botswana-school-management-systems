package com.bosams.web;

import com.bosams.domain.MarkEntity;
import com.bosams.domain.UserEntity;
import com.bosams.service.MarksService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/marks")
public class MarksController {
    private final MarksService service;
    public MarksController(MarksService service) { this.service = service; }
    @GetMapping("/entry/{examScheduleId}") public List<MarkEntity> entry(@AuthenticationPrincipal UserEntity user, @PathVariable Long examScheduleId){ return service.entry(user, examScheduleId); }
    @PutMapping("/entry/{examScheduleId}") public void draft(@AuthenticationPrincipal UserEntity user, @PathVariable Long examScheduleId,@RequestBody List<MarksService.MarkInput> payload){ service.saveDraft(user, examScheduleId, payload); }
    @PostMapping("/submit/{examScheduleId}") public void submit(@AuthenticationPrincipal UserEntity user, @PathVariable Long examScheduleId, @RequestParam(required = false) String overrideReason){ service.submit(user, examScheduleId, overrideReason); }
}
