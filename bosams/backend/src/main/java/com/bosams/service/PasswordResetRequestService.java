package com.bosams.service;

import com.bosams.common.ApiException;
import com.bosams.domain.PasswordResetRequestEntity;
import com.bosams.repository.PasswordResetRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PasswordResetRequestService {
    private final PasswordResetRequestRepository requests;

    public PasswordResetRequestService(PasswordResetRequestRepository requests) {
        this.requests = requests;
    }

    @Transactional
    public PasswordResetRequestEntity create(String admissionNo,
                                             String studentName,
                                             String guardianEmail,
                                             String guardianPhone,
                                             String reason) {
        PasswordResetRequestEntity request = new PasswordResetRequestEntity();
        request.setAdmissionNo(admissionNo.trim());
        request.setStudentName(studentName.trim());
        request.setGuardianEmail(guardianEmail == null ? null : guardianEmail.trim());
        request.setGuardianPhone(guardianPhone == null ? null : guardianPhone.trim());
        request.setReason(reason.trim());
        request.setStatus(PasswordResetRequestEntity.RequestStatus.PENDING);
        return requests.save(request);
    }

    @Transactional(readOnly = true)
    public List<PasswordResetRequestEntity> list() {
        return requests.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public PasswordResetRequestEntity updateStatus(Long requestId,
                                                   PasswordResetRequestEntity.RequestStatus status,
                                                   String adminNote) {
        PasswordResetRequestEntity request = requests.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Password reset request not found"));

        request.setStatus(status);
        request.setAdminNote(adminNote == null ? null : adminNote.trim());
        request.setResolvedAt(status == PasswordResetRequestEntity.RequestStatus.PENDING ? null : Instant.now());
        return requests.save(request);
    }
}
