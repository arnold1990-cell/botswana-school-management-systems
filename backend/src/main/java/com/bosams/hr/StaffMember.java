package com.bosams.hr;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hr_staff_members", uniqueConstraints = @UniqueConstraint(name = "uq_hr_staff_number", columnNames = {"school_id", "staff_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffMember extends HrSchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "staff_number", nullable = false, length = 40) private String staffNumber;
    @Enumerated(EnumType.STRING) @Column(name = "staff_type", nullable = false) private StaffType staffType;
    @Column(length = 20) private String title;
    @Column(name = "first_name", nullable = false, length = 100) private String firstName;
    @Column(name = "last_name", nullable = false, length = 100) private String lastName;
    @Column(name = "national_id", length = 50) private String nationalId;
    @Column(length = 20) private String gender;
    @Column(name = "date_of_birth") private LocalDate dateOfBirth;
    @Column(name = "employment_start_date") private LocalDate employmentStartDate;
    @Column(name = "employment_end_date") private LocalDate employmentEndDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StaffStatus status;
    @Column(length = 120) private String email; @Column(length = 40) private String phone;
    @Column(name = "address_line1", length = 120) private String addressLine1; @Column(name = "address_line2", length = 120) private String addressLine2;
    @Column(length = 80) private String city; @Column(length = 80) private String district; @Column(name = "postal_code", length = 20) private String postalCode;
    @Column(name = "archived_at") private Instant archivedAt; @Column(name = "archived_reason", columnDefinition = "text") private String archivedReason;

}
