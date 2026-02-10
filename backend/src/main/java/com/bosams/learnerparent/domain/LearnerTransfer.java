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

@Entity @Table(name="learner_transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearnerTransfer extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; @Enumerated(EnumType.STRING) private TransferType transferType;
private Long fromAcademicYearId; private Long fromClassRoomId; private Long fromGradeId; private Long toAcademicYearId; private Long toClassRoomId; private Long toGradeId; private LocalDate effectiveDate; private String reason; private String capturedBy; private Instant capturedAt;

}
