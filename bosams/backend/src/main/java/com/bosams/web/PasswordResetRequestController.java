package com.bosams.web;

import com.bosams.domain.PasswordResetRequestEntity;
import com.bosams.service.PasswordResetRequestService;
import com.bosams.web.dto.PasswordResetRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/password-reset-requests")
public class PasswordResetRequestController {
    private final PasswordResetRequestService service;

    public PasswordResetRequestController(PasswordResetRequestService service) {
        this.service = service;
    }

    @PostMapping
    public PasswordResetRequestDto create(@Valid @RequestBody CreatePasswordResetRequest request) {
        return PasswordResetRequestDto.from(service.create(
                request.admissionNo(),
                request.studentName(),
                request.guardianEmail(),
                request.guardianPhone(),
                request.reason()
        ));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<PasswordResetRequestDto> list() {
        return service.list().stream().map(PasswordResetRequestDto::from).toList();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public PasswordResetRequestDto updateStatus(@PathVariable Long id,
                                                @Valid @RequestBody UpdatePasswordResetRequest request) {
        return PasswordResetRequestDto.from(service.updateStatus(id, request.status(), request.adminNote()));
    }

    public record CreatePasswordResetRequest(
            @NotBlank String admissionNo,
            @NotBlank String studentName,
            String guardianEmail,
            @Pattern(regexp = "^[0-9+ -]{7,20}$", message = "Invalid guardian phone") String guardianPhone,
            @NotBlank String reason
    ) {}

    public record UpdatePasswordResetRequest(
            @NotNull PasswordResetRequestEntity.RequestStatus status,
            String adminNote
    ) {}
}
