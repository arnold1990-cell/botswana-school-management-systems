package com.bosams.service;

import com.bosams.domain.PasswordResetRequestEntity;
import com.bosams.repository.PasswordResetRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetRequestServiceTest {
    @Mock PasswordResetRequestRepository repository;
    @InjectMocks PasswordResetRequestService service;

    @Test
    void updateStatusSetsResolvedAt() {
        PasswordResetRequestEntity request = new PasswordResetRequestEntity();
        request.setId(1L);
        request.setStatus(PasswordResetRequestEntity.RequestStatus.PENDING);

        when(repository.findById(1L)).thenReturn(Optional.of(request));
        when(repository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        PasswordResetRequestEntity updated = service.updateStatus(1L, PasswordResetRequestEntity.RequestStatus.APPROVED, "done");

        assertThat(updated.getStatus()).isEqualTo(PasswordResetRequestEntity.RequestStatus.APPROVED);
        assertThat(updated.getResolvedAt()).isNotNull();
        assertThat(updated.getAdminNote()).isEqualTo("done");
    }
}
