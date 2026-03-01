package com.bosams.web;

import com.bosams.domain.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RoleDashboardController {
    @GetMapping("/teacher/dashboard")
    public Map<String, Object> teacherDashboard(@AuthenticationPrincipal UserEntity user) {
        return Map.of(
                "message", "Teacher dashboard access granted",
                "email", user.getEmail(),
                "role", user.getRole().name()
        );
    }

    @GetMapping("/admin/dashboard")
    public Map<String, Object> adminDashboard(@AuthenticationPrincipal UserEntity user) {
        return Map.of(
                "message", "Admin dashboard access granted",
                "email", user.getEmail(),
                "role", user.getRole().name()
        );
    }
}
