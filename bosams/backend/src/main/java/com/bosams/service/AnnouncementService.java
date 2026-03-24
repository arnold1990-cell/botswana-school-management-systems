package com.bosams.service;

import com.bosams.domain.AnnouncementEntity;
import com.bosams.domain.Enums;
import com.bosams.domain.UserEntity;
import com.bosams.repository.AnnouncementRepository;
import com.bosams.web.dto.AnnouncementDto;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AnnouncementService {
    private final AnnouncementRepository announcements;

    public AnnouncementService(AnnouncementRepository announcements) {
        this.announcements = announcements;
    }

    @Transactional
    public AnnouncementDto.AnnouncementResponse create(UserEntity actor, AnnouncementDto.AnnouncementRequest request) {
        if (request.targetGradeLevel() != null && (request.targetGradeLevel() < 1 || request.targetGradeLevel() > 12)) {
            throw new ValidationException("Target grade level must be between 1 and 12");
        }
        AnnouncementEntity announcement = new AnnouncementEntity();
        announcement.setTitle(request.title().trim());
        announcement.setMessage(request.message().trim());
        announcement.setTargetRole(request.targetRole());
        announcement.setTargetGradeLevel(request.targetGradeLevel());
        announcement.setExpiresAt(request.expiresAt());
        announcement.setCreatedByUserId(actor.getId());
        return toResponse(announcements.save(announcement));
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDto.AnnouncementResponse> visibleTo(Enums.Role role, Integer gradeLevel) {
        var now = OffsetDateTime.now();
        return announcements.findAllByOrderByCreatedAtDesc().stream()
                .filter(a -> a.getExpiresAt() == null || a.getExpiresAt().isAfter(now))
                .filter(a -> a.getTargetRole() == null || a.getTargetRole() == role)
                .filter(a -> a.getTargetGradeLevel() == null || (gradeLevel != null && a.getTargetGradeLevel().equals(gradeLevel)))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDto.AnnouncementResponse> all() {
        return announcements.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    private AnnouncementDto.AnnouncementResponse toResponse(AnnouncementEntity a) {
        return new AnnouncementDto.AnnouncementResponse(
                a.getId(),
                a.getTitle(),
                a.getMessage(),
                a.getTargetRole(),
                a.getTargetGradeLevel(),
                a.getCreatedByUserId(),
                a.getCreatedAt(),
                a.getExpiresAt()
        );
    }
}
