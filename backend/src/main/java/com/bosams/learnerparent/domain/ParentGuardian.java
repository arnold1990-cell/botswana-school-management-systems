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

@Entity @Table(name="parent_guardians", uniqueConstraints=@UniqueConstraint(name="uq_parent_no", columnNames={"school_id","parent_no"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentGuardian extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String parentNo; @Column(nullable=false) private String firstName; @Column(nullable=false) private String lastName; private String nationalId; private String email; private String phone; private String address; @Enumerated(EnumType.STRING) private ParentStatus status; private Instant archivedAt; private String archiveReason;

}
