package com.bosams.web;

import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/users")
public class UserController {
    private final UserRepository users; private final PasswordEncoder encoder;
    public UserController(UserRepository users, PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
    @GetMapping("/me") public UserEntity me(@AuthenticationPrincipal UserEntity me){ return me; }
    @GetMapping public List<UserEntity> list(@AuthenticationPrincipal UserEntity me){ adminOnly(me); return users.findAll(); }
    @PostMapping public UserEntity create(@AuthenticationPrincipal UserEntity me, @RequestBody CreateUser req){ adminOnly(me); UserEntity u=new UserEntity();u.setFullName(req.fullName());u.setEmail(req.email());u.setRole(req.role());u.setPasswordHash(encoder.encode(req.password()));return users.save(u); }
    private void adminOnly(UserEntity u){ if(u.getRole()!= Enums.Role.ADMIN) throw new ApiException(HttpStatus.FORBIDDEN,"FORBIDDEN","Admin only"); }
    public record CreateUser(String fullName, String email, String password, Enums.Role role) {}
}
