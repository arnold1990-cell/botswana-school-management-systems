package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="learner_absence_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearnerAbsenceNotification extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private LocalDate date; @Enumerated(EnumType.STRING) private NotificationChannel channel; @Column(columnDefinition="text") private String message; private Instant sentAt; @Enumerated(EnumType.STRING) private NotificationStatus status;

}
