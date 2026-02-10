package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

enum ApplicationStatus { SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, WAITLISTED }
enum LearnerStatus { ACTIVE, ARCHIVED, TRANSFERRED_OUT }
enum TransferType { INTERNAL_CLASS_CHANGE, TRANSFER_OUT, TRANSFER_IN }
enum ParentStatus { ACTIVE, ARCHIVED }
enum RelationshipType { MOTHER, FATHER, GUARDIAN, OTHER }
enum ActivityType { SPORT, ART, CULTURE, CLUB, OTHER }
enum ActivityRole { MEMBER, CAPTAIN, PREFECT, OTHER }
enum LeadershipRoleType { RCL, PREFECT, HEAD_BOY, HEAD_GIRL, OTHER }
enum IncidentCategory { DISCIPLINE, SAFETY, ACADEMIC, OTHER }
enum BarrierType { READING, HEARING, VISION, BEHAVIORAL, OTHER }
enum DisciplineEntryType { MERIT, DEMERIT }
enum DetentionStatus { SCHEDULED, COMPLETED, MISSED, CANCELLED }
enum AttendanceStatus { PRESENT, ABSENT, LATE, EXCUSED }
enum NotificationChannel { IN_APP, EMAIL, SMS }
enum NotificationStatus { PENDING, SENT, FAILED }

@MappedSuperclass
abstract class SchoolScopedEntity extends AuditableEntity {
    @Column(name = "school_id", nullable = false)
    private Long schoolId;
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
}

@Entity @Table(name="learner_applications", uniqueConstraints=@UniqueConstraint(name="uq_learner_application_no", columnNames={"school_id","application_no"}))
class LearnerApplication extends SchoolScopedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name="application_no",nullable=false) private String applicationNo;
    @Column(name="applied_date",nullable=false) private LocalDate appliedDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private ApplicationStatus status;
    private Long preferredGradeId; private Long preferredAcademicYearId;
    @Column(nullable=false) private String learnerFirstName; @Column(nullable=false) private String learnerLastName;
    private LocalDate dateOfBirth; private String gender; private String previousSchool;
    @Column(columnDefinition="text") private String notes; @Column(columnDefinition="text") private String decisionNote;
    private Instant decidedAt; private String decidedBy;
    public Long getId(){return id;} public String getApplicationNo(){return applicationNo;} public void setApplicationNo(String v){applicationNo=v;}
    public LocalDate getAppliedDate(){return appliedDate;} public void setAppliedDate(LocalDate v){appliedDate=v;}
    public ApplicationStatus getStatus(){return status;} public void setStatus(ApplicationStatus v){status=v;}
    public Long getPreferredGradeId(){return preferredGradeId;} public void setPreferredGradeId(Long v){preferredGradeId=v;}
    public Long getPreferredAcademicYearId(){return preferredAcademicYearId;} public void setPreferredAcademicYearId(Long v){preferredAcademicYearId=v;}
    public String getLearnerFirstName(){return learnerFirstName;} public void setLearnerFirstName(String v){learnerFirstName=v;}
    public String getLearnerLastName(){return learnerLastName;} public void setLearnerLastName(String v){learnerLastName=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){dateOfBirth=v;}
    public String getGender(){return gender;} public void setGender(String v){gender=v;}
    public String getPreviousSchool(){return previousSchool;} public void setPreviousSchool(String v){previousSchool=v;}
    public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
    public String getDecisionNote(){return decisionNote;} public void setDecisionNote(String v){decisionNote=v;}
    public Instant getDecidedAt(){return decidedAt;} public void setDecidedAt(Instant v){decidedAt=v;}
    public String getDecidedBy(){return decidedBy;} public void setDecidedBy(String v){decidedBy=v;}
}

@Entity @Table(name="learners", uniqueConstraints=@UniqueConstraint(name="uq_learner_no", columnNames={"school_id","learner_no"}))
class Learner extends SchoolScopedEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="learner_no",nullable=false) private String learnerNo;
    @Column(nullable=false) private String firstName; @Column(nullable=false) private String lastName;
    private LocalDate dateOfBirth; private String gender; private String nationalId; private String homeLanguage;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private LearnerStatus status;
    private Long currentAcademicYearId; private Long currentGradeId; private Long currentClassRoomId; private Long houseId;
    private LocalDate admissionDate; private Instant archivedAt; private String archiveReason;
    public Long getId(){return id;} public String getLearnerNo(){return learnerNo;} public void setLearnerNo(String v){learnerNo=v;}
    public String getFirstName(){return firstName;} public void setFirstName(String v){firstName=v;} public String getLastName(){return lastName;} public void setLastName(String v){lastName=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){dateOfBirth=v;} public String getGender(){return gender;} public void setGender(String v){gender=v;}
    public String getNationalId(){return nationalId;} public void setNationalId(String v){nationalId=v;} public String getHomeLanguage(){return homeLanguage;} public void setHomeLanguage(String v){homeLanguage=v;}
    public LearnerStatus getStatus(){return status;} public void setStatus(LearnerStatus v){status=v;} public Long getCurrentAcademicYearId(){return currentAcademicYearId;} public void setCurrentAcademicYearId(Long v){currentAcademicYearId=v;}
    public Long getCurrentGradeId(){return currentGradeId;} public void setCurrentGradeId(Long v){currentGradeId=v;} public Long getCurrentClassRoomId(){return currentClassRoomId;} public void setCurrentClassRoomId(Long v){currentClassRoomId=v;}
    public Long getHouseId(){return houseId;} public void setHouseId(Long v){houseId=v;} public LocalDate getAdmissionDate(){return admissionDate;} public void setAdmissionDate(LocalDate v){admissionDate=v;}
    public Instant getArchivedAt(){return archivedAt;} public void setArchivedAt(Instant v){archivedAt=v;} public String getArchiveReason(){return archiveReason;} public void setArchiveReason(String v){archiveReason=v;}
}

@Entity @Table(name="learner_transfers")
class LearnerTransfer extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; @Enumerated(EnumType.STRING) private TransferType transferType;
private Long fromAcademicYearId; private Long fromClassRoomId; private Long fromGradeId; private Long toAcademicYearId; private Long toClassRoomId; private Long toGradeId; private LocalDate effectiveDate; private String reason; private String capturedBy; private Instant capturedAt;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public TransferType getTransferType(){return transferType;} public void setTransferType(TransferType v){transferType=v;}
public Long getFromAcademicYearId(){return fromAcademicYearId;} public void setFromAcademicYearId(Long v){fromAcademicYearId=v;} public Long getFromClassRoomId(){return fromClassRoomId;} public void setFromClassRoomId(Long v){fromClassRoomId=v;} public Long getFromGradeId(){return fromGradeId;} public void setFromGradeId(Long v){fromGradeId=v;}
public Long getToAcademicYearId(){return toAcademicYearId;} public void setToAcademicYearId(Long v){toAcademicYearId=v;} public Long getToClassRoomId(){return toClassRoomId;} public void setToClassRoomId(Long v){toClassRoomId=v;} public Long getToGradeId(){return toGradeId;} public void setToGradeId(Long v){toGradeId=v;}
public LocalDate getEffectiveDate(){return effectiveDate;} public void setEffectiveDate(LocalDate v){effectiveDate=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;} public String getCapturedBy(){return capturedBy;} public void setCapturedBy(String v){capturedBy=v;} public Instant getCapturedAt(){return capturedAt;} public void setCapturedAt(Instant v){capturedAt=v;}
}

@Entity @Table(name="parent_guardians", uniqueConstraints=@UniqueConstraint(name="uq_parent_no", columnNames={"school_id","parent_no"}))
class ParentGuardian extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String parentNo; @Column(nullable=false) private String firstName; @Column(nullable=false) private String lastName; private String nationalId; private String email; private String phone; private String address; @Enumerated(EnumType.STRING) private ParentStatus status; private Instant archivedAt; private String archiveReason;
public Long getId(){return id;} public String getParentNo(){return parentNo;} public void setParentNo(String v){parentNo=v;} public String getFirstName(){return firstName;} public void setFirstName(String v){firstName=v;} public String getLastName(){return lastName;} public void setLastName(String v){lastName=v;} public String getNationalId(){return nationalId;} public void setNationalId(String v){nationalId=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getAddress(){return address;} public void setAddress(String v){address=v;} public ParentStatus getStatus(){return status;} public void setStatus(ParentStatus v){status=v;} public Instant getArchivedAt(){return archivedAt;} public void setArchivedAt(Instant v){archivedAt=v;} public String getArchiveReason(){return archiveReason;} public void setArchiveReason(String v){archiveReason=v;}
}

@Entity @Table(name="learner_parent_links", uniqueConstraints=@UniqueConstraint(name="uq_learner_parent_link", columnNames={"school_id","learner_id","parent_id"}))
class LearnerParentLink extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long parentId; @Enumerated(EnumType.STRING) private RelationshipType relationshipType; private boolean isPrimaryContact; private boolean livesWithLearner;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public Long getParentId(){return parentId;} public void setParentId(Long v){parentId=v;} public RelationshipType getRelationshipType(){return relationshipType;} public void setRelationshipType(RelationshipType v){relationshipType=v;} public boolean isPrimaryContact(){return isPrimaryContact;} public void setPrimaryContact(boolean v){isPrimaryContact=v;} public boolean isLivesWithLearner(){return livesWithLearner;} public void setLivesWithLearner(boolean v){livesWithLearner=v;}
}

@Entity @Table(name="learner_activity_memberships", uniqueConstraints=@UniqueConstraint(name="uq_learner_activity", columnNames={"school_id","learner_id","activity_type","activity_id"}))
class LearnerActivityMembership extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; @Enumerated(EnumType.STRING) private ActivityType activityType; private Long activityId; @Enumerated(EnumType.STRING) private ActivityRole role; private LocalDate startDate; private LocalDate endDate;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public ActivityType getActivityType(){return activityType;} public void setActivityType(ActivityType v){activityType=v;} public Long getActivityId(){return activityId;} public void setActivityId(Long v){activityId=v;} public ActivityRole getRole(){return role;} public void setRole(ActivityRole v){role=v;} public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}}

@Entity @Table(name="learner_transport_assignments", uniqueConstraints=@UniqueConstraint(name="uq_transport_assign", columnNames={"school_id","learner_id","start_date"}))
class LearnerTransportAssignment extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long busRouteId; private Long ticketTypeId; private LocalDate startDate; private LocalDate endDate;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public Long getBusRouteId(){return busRouteId;} public void setBusRouteId(Long v){busRouteId=v;} public Long getTicketTypeId(){return ticketTypeId;} public void setTicketTypeId(Long v){ticketTypeId=v;} public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}}

@Entity @Table(name="learner_leadership_roles", uniqueConstraints=@UniqueConstraint(name="uq_leadership", columnNames={"school_id","learner_id","role_type","academic_year_id"}))
class LearnerLeadershipRole extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; @Enumerated(EnumType.STRING) private LeadershipRoleType roleType; private Long academicYearId; private LocalDate startDate; private LocalDate endDate; private String notes;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public LeadershipRoleType getRoleType(){return roleType;} public void setRoleType(LeadershipRoleType v){roleType=v;} public Long getAcademicYearId(){return academicYearId;} public void setAcademicYearId(Long v){academicYearId=v;} public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;}}

@Entity @Table(name="learner_incidents")
class LearnerIncident extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private LocalDate incidentDate; @Enumerated(EnumType.STRING) private IncidentCategory category; @Column(columnDefinition="text") private String description; @Column(columnDefinition="text") private String actionTaken; private String reportedBy;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public LocalDate getIncidentDate(){return incidentDate;} public void setIncidentDate(LocalDate v){incidentDate=v;} public IncidentCategory getCategory(){return category;} public void setCategory(IncidentCategory v){category=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public String getActionTaken(){return actionTaken;} public void setActionTaken(String v){actionTaken=v;} public String getReportedBy(){return reportedBy;} public void setReportedBy(String v){reportedBy=v;}}

@Entity @Table(name="learner_learning_barriers")
class LearnerLearningBarrier extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; @Enumerated(EnumType.STRING) private BarrierType barrierType; @Column(columnDefinition="text") private String notes; private LocalDate identifiedDate;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public BarrierType getBarrierType(){return barrierType;} public void setBarrierType(BarrierType v){barrierType=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public LocalDate getIdentifiedDate(){return identifiedDate;} public void setIdentifiedDate(LocalDate v){identifiedDate=v;}}

@Entity @Table(name="learner_mentor_assignments", uniqueConstraints=@UniqueConstraint(name="uq_mentor_assignment", columnNames={"school_id","learner_id","start_date"}))
class LearnerMentorAssignment extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long mentorStaffId; private LocalDate startDate; private LocalDate endDate; @Column(columnDefinition="text") private String notes;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public Long getMentorStaffId(){return mentorStaffId;} public void setMentorStaffId(Long v){mentorStaffId=v;} public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;}}

@Entity @Table(name="learner_discipline_entries")
class LearnerDisciplineEntry extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private LocalDate entryDate; @Enumerated(EnumType.STRING) private DisciplineEntryType entryType; private Long codeId; private Integer points; private String notes; private String capturedBy;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public LocalDate getEntryDate(){return entryDate;} public void setEntryDate(LocalDate v){entryDate=v;} public DisciplineEntryType getEntryType(){return entryType;} public void setEntryType(DisciplineEntryType v){entryType=v;} public Long getCodeId(){return codeId;} public void setCodeId(Long v){codeId=v;} public Integer getPoints(){return points;} public void setPoints(Integer v){points=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public String getCapturedBy(){return capturedBy;} public void setCapturedBy(String v){capturedBy=v;}}

@Entity @Table(name="learner_detention_actions")
class LearnerDetentionAction extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private LocalDate scheduledDate; private Integer durationMinutes; private String reason; @Enumerated(EnumType.STRING) private DetentionStatus status;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public LocalDate getScheduledDate(){return scheduledDate;} public void setScheduledDate(LocalDate v){scheduledDate=v;} public Integer getDurationMinutes(){return durationMinutes;} public void setDurationMinutes(Integer v){durationMinutes=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;} public DetentionStatus getStatus(){return status;} public void setStatus(DetentionStatus v){status=v;}}

@Entity @Table(name="learner_attendance_records", uniqueConstraints=@UniqueConstraint(name="uq_attendance", columnNames={"school_id","learner_id","date","period"}))
class LearnerAttendanceRecord extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long academicYearId; private Long termId; private LocalDate date; private Integer period; @Enumerated(EnumType.STRING) private AttendanceStatus status; private String notes; private String capturedBy; private Instant capturedAt;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public Long getAcademicYearId(){return academicYearId;} public void setAcademicYearId(Long v){academicYearId=v;} public Long getTermId(){return termId;} public void setTermId(Long v){termId=v;} public LocalDate getDate(){return date;} public void setDate(LocalDate v){date=v;} public Integer getPeriod(){return period;} public void setPeriod(Integer v){period=v;} public AttendanceStatus getStatus(){return status;} public void setStatus(AttendanceStatus v){status=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public String getCapturedBy(){return capturedBy;} public void setCapturedBy(String v){capturedBy=v;} public Instant getCapturedAt(){return capturedAt;} public void setCapturedAt(Instant v){capturedAt=v;}}

@Entity @Table(name="learner_absence_notifications")
class LearnerAbsenceNotification extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private LocalDate date; @Enumerated(EnumType.STRING) private NotificationChannel channel; @Column(columnDefinition="text") private String message; private Instant sentAt; @Enumerated(EnumType.STRING) private NotificationStatus status;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public LocalDate getDate(){return date;} public void setDate(LocalDate v){date=v;} public NotificationChannel getChannel(){return channel;} public void setChannel(NotificationChannel v){channel=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;} public Instant getSentAt(){return sentAt;} public void setSentAt(Instant v){sentAt=v;} public NotificationStatus getStatus(){return status;} public void setStatus(NotificationStatus v){status=v;}}
