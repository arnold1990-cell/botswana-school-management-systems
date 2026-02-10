package com.bosams.hr;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "hr_staff_members", uniqueConstraints = @UniqueConstraint(name = "uq_hr_staff_number", columnNames = {"school_id", "staff_number"}))
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
    public Long getId() { return id; } public String getStaffNumber() { return staffNumber; } public void setStaffNumber(String v) { staffNumber = v; }
    public StaffType getStaffType() { return staffType; } public void setStaffType(StaffType v) { staffType = v; }
    public String getTitle() { return title; } public void setTitle(String v) { title = v; }
    public String getFirstName() { return firstName; } public void setFirstName(String v) { firstName = v; }
    public String getLastName() { return lastName; } public void setLastName(String v) { lastName = v; }
    public String getNationalId() { return nationalId; } public void setNationalId(String v) { nationalId = v; }
    public String getGender() { return gender; } public void setGender(String v) { gender = v; }
    public LocalDate getDateOfBirth() { return dateOfBirth; } public void setDateOfBirth(LocalDate v) { dateOfBirth = v; }
    public LocalDate getEmploymentStartDate() { return employmentStartDate; } public void setEmploymentStartDate(LocalDate v) { employmentStartDate = v; }
    public LocalDate getEmploymentEndDate() { return employmentEndDate; } public void setEmploymentEndDate(LocalDate v) { employmentEndDate = v; }
    public StaffStatus getStatus() { return status; } public void setStatus(StaffStatus v) { status = v; }
    public String getEmail() { return email; } public void setEmail(String v) { email = v; }
    public String getPhone() { return phone; } public void setPhone(String v) { phone = v; }
    public String getAddressLine1() { return addressLine1; } public void setAddressLine1(String v) { addressLine1 = v; }
    public String getAddressLine2() { return addressLine2; } public void setAddressLine2(String v) { addressLine2 = v; }
    public String getCity() { return city; } public void setCity(String v) { city = v; }
    public String getDistrict() { return district; } public void setDistrict(String v) { district = v; }
    public String getPostalCode() { return postalCode; } public void setPostalCode(String v) { postalCode = v; }
    public Instant getArchivedAt() { return archivedAt; } public void setArchivedAt(Instant v) { archivedAt = v; }
    public String getArchivedReason() { return archivedReason; } public void setArchivedReason(String v) { archivedReason = v; }
}
