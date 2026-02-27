package com.bosams.web;

import com.bosams.domain.UserEntity;
import com.bosams.service.AuthService;
import jakarta.validation.constraints.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth; public AuthController(AuthService auth){this.auth=auth;}
    @PostMapping("/login") public Map<String,String> login(@RequestBody LoginReq req){ return auth.login(req.email(), req.password()); }
    @PostMapping("/refresh") public Map<String,String> refresh(@RequestBody Map<String,String> req){ return auth.refresh(req.get("refreshToken")); }
    @PostMapping("/logout") public void logout(@AuthenticationPrincipal UserEntity user, @RequestBody Map<String,String> req){ auth.logout(user, req.get("refreshToken")); }
    public record LoginReq(@Email String email, @NotBlank String password) {}
}
