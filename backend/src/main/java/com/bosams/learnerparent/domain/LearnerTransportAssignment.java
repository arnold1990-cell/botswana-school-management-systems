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

@Entity @Table(name="learner_transport_assignments", uniqueConstraints=@UniqueConstraint(name="uq_transport_assign", columnNames={"school_id","learner_id","start_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearnerTransportAssignment extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long busRouteId; private Long ticketTypeId; private LocalDate startDate; private LocalDate endDate;

}
