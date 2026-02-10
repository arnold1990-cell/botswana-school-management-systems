package com.bosams.hr;

import com.bosams.common.ConflictException;
import com.bosams.common.DuplicateResourceException;
import com.bosams.common.NotFoundException;
import com.bosams.hr.*;
import com.bosams.hr.*;
import com.bosams.hr.AppraisalRecordRepository;
import com.bosams.hr.EducatorSubjectExperienceRepository;
import com.bosams.hr.RegisterClassAssignmentRepository;
import com.bosams.hr.StaffAttendanceRecordRepository;
import com.bosams.hr.StaffLeaveRequestRepository;
import com.bosams.hr.StaffMemberRepository;
import com.bosams.hr.TrainingAttendanceRepository;
import com.bosams.hr.TrainingProgramRepository;
import com.bosams.schoolsetup.repository.AcademicYearRepository;
import com.bosams.schoolsetup.repository.ClassRoomRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HrService {
    private final StaffMemberRepository staffRepo;
    private final EducatorSubjectExperienceRepository expRepo;
    private final RegisterClassAssignmentRepository registerRepo;
    private final StaffLeaveRequestRepository leaveRepo;
    private final StaffAttendanceRecordRepository attendanceRepo;
    private final TrainingProgramRepository trainingRepo;
    private final TrainingAttendanceRepository trainingAttendanceRepo;
    private final AppraisalRecordRepository appraisalRepo;
    private final AcademicYearRepository academicYears;
    private final ClassRoomRepository classRooms;

    public HrService(StaffMemberRepository staffRepo, EducatorSubjectExperienceRepository expRepo, RegisterClassAssignmentRepository registerRepo,
              StaffLeaveRequestRepository leaveRepo, StaffAttendanceRecordRepository attendanceRepo, TrainingProgramRepository trainingRepo,
              TrainingAttendanceRepository trainingAttendanceRepo, AppraisalRecordRepository appraisalRepo, AcademicYearRepository academicYears,
              ClassRoomRepository classRooms) {
        this.staffRepo = staffRepo; this.expRepo = expRepo; this.registerRepo = registerRepo; this.leaveRepo = leaveRepo; this.attendanceRepo = attendanceRepo;
        this.trainingRepo = trainingRepo; this.trainingAttendanceRepo = trainingAttendanceRepo; this.appraisalRepo = appraisalRepo; this.academicYears = academicYears; this.classRooms = classRooms;
    }

    public StaffResponse createStaff(StaffUpsertRequest r, String actor){ ensureStaffUnique(r.schoolId(), r.staffNumber(), null); var s = new StaffMember(); applyStaff(s, r); s.setStatus(StaffStatus.ACTIVE); return map(staffRepo.save(s)); }
    public StaffResponse getStaff(Long id){ return map(reqStaff(id)); }
    public Page<StaffResponse> listStaff(Long schoolId, StaffStatus status, StaffType type, Pageable p){
        if(status!=null && type!=null) return staffRepo.findBySchoolIdAndStatusAndStaffType(schoolId,status,type,p).map(this::map);
        if(status!=null) return staffRepo.findBySchoolIdAndStatus(schoolId,status,p).map(this::map);
        if(type!=null) return staffRepo.findBySchoolIdAndStaffType(schoolId,type,p).map(this::map);
        return staffRepo.findBySchoolId(schoolId,p).map(this::map);
    }
    public StaffResponse updateStaff(Long id, StaffUpsertRequest r, String actor){ var s = reqStaff(id); ensureStaffUnique(r.schoolId(), r.staffNumber(), id); applyStaff(s,r); return map(staffRepo.save(s)); }
    public StaffResponse archive(Long id, StaffArchiveRequest r, String actor){ var s=reqStaff(id); if(s.getStatus()==StaffStatus.ARCHIVED) throw new ConflictException("Staff already archived"); s.setStatus(StaffStatus.ARCHIVED); s.setArchivedAt(Instant.now()); s.setArchivedReason(r.reason()); return map(staffRepo.save(s)); }
    public StaffResponse restore(Long id, String actor){ var s=reqStaff(id); if(s.getStatus()!=StaffStatus.ARCHIVED) throw new ConflictException("Staff is not archived"); s.setStatus(StaffStatus.ACTIVE); s.setArchivedAt(null); s.setArchivedReason(null); return map(staffRepo.save(s)); }

    public SubjectExperienceResponse addSubjectExperience(Long staffId, SubjectExperienceRequest r, String actor){ var st=reqEducatorInSchool(staffId, r.schoolId()); ensureSchoolReference(r.schoolId(), null, null, r.subjectId()); var e=new EducatorSubjectExperience(); e.setSchoolId(r.schoolId()); e.setStaffId(staffId); e.setSubjectId(r.subjectId()); e.setYearsExperience(r.yearsExperience()); e.setNotes(r.notes()); return map(expRepo.save(e)); }
    public Page<SubjectExperienceResponse> listSubjectExperience(Long schoolId, Long staffId, Pageable p){ return expRepo.findBySchoolIdAndStaffId(schoolId, staffId, p).map(this::map); }
    public void deleteSubjectExperience(Long id){ expRepo.deleteById(id); }

    public RegisterClassAssignmentResponse assignRegisterClass(RegisterClassAssignRequest r, String actor){ reqEducatorInSchool(r.staffId(), r.schoolId()); ensureSchoolReference(r.schoolId(), r.academicYearId(), r.classRoomId(), null); var a=new RegisterClassAssignment(); a.setSchoolId(r.schoolId()); a.setStaffId(r.staffId()); a.setClassRoomId(r.classRoomId()); a.setAcademicYearId(r.academicYearId()); a.setRole(r.role()); return map(registerRepo.save(a)); }
    public Page<RegisterClassAssignmentResponse> listRegisterClass(Long schoolId, Long yearId, Long classId, Pageable p){ if(yearId!=null && classId!=null) return registerRepo.findBySchoolIdAndAcademicYearIdAndClassRoomId(schoolId,yearId,classId,p).map(this::map); if(yearId!=null) return registerRepo.findBySchoolIdAndAcademicYearId(schoolId,yearId,p).map(this::map); return registerRepo.findBySchoolId(schoolId,p).map(this::map); }
    public void deleteRegisterClass(Long id){ registerRepo.deleteById(id); }

    public LeaveRequestResponse createLeave(LeaveRequestCreate r, String actor){ if(r.endDate().isBefore(r.startDate())) throw new ConflictException("endDate must be >= startDate"); reqStaffInSchool(r.staffId(), r.schoolId()); var l=new StaffLeaveRequest(); l.setSchoolId(r.schoolId()); l.setStaffId(r.staffId()); l.setLeaveType(r.leaveType()); l.setStartDate(r.startDate()); l.setEndDate(r.endDate()); l.setReason(r.reason()); l.setStatus(LeaveStatus.PENDING); return map(leaveRepo.save(l)); }
    public LeaveRequestResponse getLeave(Long id){ return map(reqLeave(id)); }
    public Page<LeaveRequestResponse> listLeave(Long schoolId, LeaveStatus status, Long staffId, LocalDate from, LocalDate to, Pageable p){ if(from!=null && to!=null) return leaveRepo.findBySchoolIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(schoolId,from,to,p).map(this::map); if(status!=null && staffId!=null) return leaveRepo.findBySchoolIdAndStatusAndStaffId(schoolId,status,staffId,p).map(this::map); if(status!=null) return leaveRepo.findBySchoolIdAndStatus(schoolId,status,p).map(this::map); if(staffId!=null) return leaveRepo.findBySchoolIdAndStaffId(schoolId,staffId,p).map(this::map); return leaveRepo.findBySchoolId(schoolId,p).map(this::map); }
    public LeaveRequestResponse approveLeave(Long id, LeaveDecisionRequest r, String actor){ return decide(id, LeaveStatus.APPROVED, r, actor); }
    public LeaveRequestResponse rejectLeave(Long id, LeaveDecisionRequest r, String actor){ return decide(id, LeaveStatus.REJECTED, r, actor); }
    public LeaveRequestResponse cancelLeave(Long id, LeaveDecisionRequest r, String actor){ return decide(id, LeaveStatus.CANCELLED, r, actor); }

    public List<AttendanceResponse> createAttendance(AttendanceCreateRequest r, String actor){
        if(r.records()!=null && !r.records().isEmpty()) return r.records().stream().map(i->createAttendanceRecord(r.schoolId(),r.date(),i.staffId(),i.status(),i.notes(),actor)).toList();
        return List.of(createAttendanceRecord(r.schoolId(),r.date(),r.staffId(),r.status(),r.notes(),actor));
    }
    public Page<AttendanceResponse> attendanceByDate(Long schoolId, LocalDate date, Pageable p){ return attendanceRepo.findBySchoolIdAndDate(schoolId,date,p).map(this::map); }
    public Page<AttendanceResponse> attendanceByStaff(Long schoolId, Long staffId, LocalDate from, LocalDate to, Pageable p){ return attendanceRepo.findBySchoolIdAndStaffIdAndDateBetween(schoolId,staffId,from,to,p).map(this::map); }

    public DashboardSummary dashboard(Long schoolId, LocalDate from, LocalDate to){
        long totalStaff = staffRepo.findBySchoolId(schoolId, Pageable.unpaged()).getTotalElements();
        long totalAtt = attendanceRepo.countBySchoolIdAndDateBetween(schoolId, from, to);
        long present = attendanceRepo.countBySchoolIdAndDateBetweenAndStatus(schoolId, from, to, StaffAttendanceStatus.PRESENT);
        long absent = attendanceRepo.countBySchoolIdAndDateBetweenAndStatus(schoolId, from, to, StaffAttendanceStatus.ABSENT);
        long late = attendanceRepo.countBySchoolIdAndDateBetweenAndStatus(schoolId, from, to, StaffAttendanceStatus.LATE);
        double attendanceRate = totalAtt == 0 ? 0 : ((double) present / totalAtt);
        long pending = leaveRepo.countBySchoolIdAndStatus(schoolId, LeaveStatus.PENDING);
        var grouped = attendanceRepo.findBySchoolIdAndDateBetween(schoolId, from, to, Pageable.unpaged()).stream().filter(a->a.getStatus()==StaffAttendanceStatus.ABSENT).collect(Collectors.groupingBy(StaffAttendanceRecord::getStaffId, Collectors.counting()));
        var top = grouped.entrySet().stream().sorted(Map.Entry.<Long,Long>comparingByValue(Comparator.reverseOrder())).limit(5).map(e -> new DashboardTopAbsent(e.getKey(), e.getValue())).toList();
        return new DashboardSummary(totalStaff, attendanceRate, absent, late, pending, top);
    }

    public TrainingProgramResponse createTraining(TrainingProgramUpsertRequest r, String actor){ var t=new TrainingProgram(); applyTraining(t,r); return map(trainingRepo.save(t)); }
    public TrainingProgramResponse getTraining(Long id){ return map(reqTraining(id)); }
    public Page<TrainingProgramResponse> listTraining(Long schoolId, LocalDate from, LocalDate to, Pageable p){ if(from!=null && to!=null) return trainingRepo.findBySchoolIdAndStartDateBetween(schoolId,from,to,p).map(this::map); return trainingRepo.findBySchoolId(schoolId,p).map(this::map); }
    public TrainingProgramResponse updateTraining(Long id, TrainingProgramUpsertRequest r, String actor){ var t=reqTraining(id); applyTraining(t,r); return map(trainingRepo.save(t)); }
    public void deleteTraining(Long id){ trainingRepo.deleteById(id); }
    public List<TrainingAttendanceResponse> addTrainingAttendance(Long programId, TrainingAttendanceBulkRequest r, String actor){ reqTraining(programId); return r.records().stream().map(i->{reqStaff(i.staffId()); var a=new TrainingAttendance(); a.setSchoolId(r.schoolId()); a.setTrainingProgramId(programId); a.setStaffId(i.staffId()); a.setAttended(i.attended()); a.setCertificateUrl(i.certificateUrl()); return map(trainingAttendanceRepo.save(a));}).toList(); }
    public Page<TrainingAttendanceResponse> listTrainingAttendance(Long schoolId, Long programId, Pageable p){ return trainingAttendanceRepo.findBySchoolIdAndTrainingProgramId(schoolId,programId,p).map(this::map); }

    public AppraisalResponse createAppraisal(AppraisalCreateRequest r, String actor){ reqStaff(r.staffId()); var a = new AppraisalRecord(); a.setSchoolId(r.schoolId()); a.setStaffId(r.staffId()); a.setAppraisalDate(r.appraisalDate()); a.setAppraisalType(r.appraisalType()); a.setReviewerName(r.reviewerName()); a.setScore(r.score()); a.setSummary(r.summary()); a.setRecommendations(r.recommendations()); return map(appraisalRepo.save(a)); }
    public AppraisalResponse getAppraisal(Long id){ return map(reqAppraisal(id)); }
    public Page<AppraisalResponse> listAppraisal(Long schoolId, Long staffId, AppraisalType type, LocalDate from, LocalDate to, Pageable p){ if(from!=null && to!=null) return appraisalRepo.findBySchoolIdAndAppraisalDateBetween(schoolId,from,to,p).map(this::map); if(staffId!=null && type!=null) return appraisalRepo.findBySchoolIdAndStaffIdAndAppraisalType(schoolId,staffId,type,p).map(this::map); if(staffId!=null) return appraisalRepo.findBySchoolIdAndStaffId(schoolId,staffId,p).map(this::map); if(type!=null) return appraisalRepo.findBySchoolIdAndAppraisalType(schoolId,type,p).map(this::map); return appraisalRepo.findBySchoolId(schoolId,p).map(this::map); }

    private AttendanceResponse createAttendanceRecord(Long schoolId, LocalDate date, Long staffId, StaffAttendanceStatus status, String notes, String actor){
        reqStaffInSchool(staffId, schoolId); if(attendanceRepo.existsBySchoolIdAndStaffIdAndDate(schoolId,staffId,date)) throw new ConflictException("Attendance already captured for staff/date");
        var a=new StaffAttendanceRecord(); a.setSchoolId(schoolId); a.setStaffId(staffId); a.setDate(date);
        var finalStatus = status;
        if(status == StaffAttendanceStatus.ABSENT && leaveRepo.existsBySchoolIdAndStaffIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(schoolId,staffId,LeaveStatus.APPROVED,date,date)) finalStatus = StaffAttendanceStatus.OFFICIAL_DUTY;
        a.setStatus(finalStatus); a.setNotes(notes); a.setCapturedBy(actor); a.setCapturedAt(Instant.now()); return map(attendanceRepo.save(a));
    }

    private LeaveRequestResponse decide(Long id, LeaveStatus target, LeaveDecisionRequest r, String actor){ var leave=reqLeave(id); if(leave.getStatus()!=LeaveStatus.PENDING) throw new ConflictException("Only pending requests can be decided"); leave.setStatus(target); leave.setDecidedAt(Instant.now()); leave.setDecidedBy(actor); leave.setDecisionNote(r.note()); return map(leaveRepo.save(leave)); }
    private void applyStaff(StaffMember s, StaffUpsertRequest r){ s.setSchoolId(r.schoolId()); s.setStaffNumber(r.staffNumber()); s.setStaffType(r.staffType()); s.setTitle(r.title()); s.setFirstName(r.firstName()); s.setLastName(r.lastName()); s.setNationalId(r.nationalId()); s.setGender(r.gender()); s.setDateOfBirth(r.dateOfBirth()); s.setEmploymentStartDate(r.employmentStartDate()); s.setEmploymentEndDate(r.employmentEndDate()); s.setEmail(r.email()); s.setPhone(r.phone()); s.setAddressLine1(r.addressLine1()); s.setAddressLine2(r.addressLine2()); s.setCity(r.city()); s.setDistrict(r.district()); s.setPostalCode(r.postalCode()); }
    private void applyTraining(TrainingProgram t, TrainingProgramUpsertRequest r){ t.setSchoolId(r.schoolId()); t.setTitle(r.title()); t.setProvider(r.provider()); t.setStartDate(r.startDate()); t.setEndDate(r.endDate()); t.setCategory(r.category()); t.setNotes(r.notes()); }
    private void ensureStaffUnique(Long schoolId, String number, Long id){ var dup = id==null ? staffRepo.existsBySchoolIdAndStaffNumber(schoolId,number) : staffRepo.existsBySchoolIdAndStaffNumberAndIdNot(schoolId,number,id); if(dup) throw new DuplicateResourceException("StaffMember","staffNumber",number,schoolId); }
    private StaffMember reqStaff(Long id){ return staffRepo.findById(id).orElseThrow(() -> new NotFoundException("Staff member not found")); }
    private StaffMember reqEducator(Long staffId){ var staff=reqStaff(staffId); if(staff.getStaffType()!=StaffType.EDUCATOR) throw new ConflictException("Only educator staff can be used for this operation"); return staff; }
    private StaffMember reqStaffInSchool(Long staffId, Long schoolId){ var staff=reqStaff(staffId); if(!staff.getSchoolId().equals(schoolId)) throw new ConflictException("School mismatch"); return staff; }
    private StaffMember reqEducatorInSchool(Long staffId, Long schoolId){ var staff=reqEducator(staffId); if(!staff.getSchoolId().equals(schoolId)) throw new ConflictException("School mismatch"); return staff; }
    private StaffLeaveRequest reqLeave(Long id){ return leaveRepo.findById(id).orElseThrow(() -> new NotFoundException("Leave request not found")); }
    private TrainingProgram reqTraining(Long id){ return trainingRepo.findById(id).orElseThrow(() -> new NotFoundException("Training program not found")); }
    private AppraisalRecord reqAppraisal(Long id){ return appraisalRepo.findById(id).orElseThrow(() -> new NotFoundException("Appraisal not found")); }
    private void ensureSchoolReference(Long schoolId, Long yearId, Long classId, Long subjectId){
        if(yearId != null && academicYears.findById(yearId).filter(y -> y.getSchool().getId().equals(schoolId)).isEmpty()) throw new ConflictException("academicYearId does not belong to school");
        if(classId != null && classRooms.findById(classId).filter(c -> c.getSchool().getId().equals(schoolId)).isEmpty()) throw new ConflictException("classRoomId does not belong to school");
        if(subjectId != null && subjectId <= 0) throw new ConflictException("subjectId must be positive");
    }

    private StaffResponse map(StaffMember s){ return new StaffResponse(s.getId(), s.getSchoolId(), s.getStaffNumber(), s.getStaffType(), s.getFirstName(), s.getLastName(), s.getStatus(), s.getEmail(), s.getPhone(), s.getArchivedAt(), s.getArchivedReason(), s.getCreatedAt(), s.getUpdatedAt(), s.getCreatedBy(), s.getUpdatedBy()); }
    private SubjectExperienceResponse map(EducatorSubjectExperience e){ return new SubjectExperienceResponse(e.getId(), e.getSchoolId(), e.getStaffId(), e.getSubjectId(), e.getYearsExperience(), e.getNotes()); }
    private RegisterClassAssignmentResponse map(RegisterClassAssignment a){ return new RegisterClassAssignmentResponse(a.getId(), a.getSchoolId(), a.getStaffId(), a.getClassRoomId(), a.getAcademicYearId(), a.getRole()); }
    private LeaveRequestResponse map(StaffLeaveRequest l){ return new LeaveRequestResponse(l.getId(), l.getSchoolId(), l.getStaffId(), l.getLeaveType(), l.getStartDate(), l.getEndDate(), l.getReason(), l.getStatus(), l.getDecidedBy(), l.getDecidedAt(), l.getDecisionNote()); }
    private AttendanceResponse map(StaffAttendanceRecord a){ return new AttendanceResponse(a.getId(), a.getSchoolId(), a.getStaffId(), a.getDate(), a.getStatus(), a.getNotes(), a.getCapturedBy(), a.getCapturedAt()); }
    private TrainingProgramResponse map(TrainingProgram t){ return new TrainingProgramResponse(t.getId(), t.getSchoolId(), t.getTitle(), t.getProvider(), t.getStartDate(), t.getEndDate(), t.getCategory(), t.getNotes()); }
    private TrainingAttendanceResponse map(TrainingAttendance t){ return new TrainingAttendanceResponse(t.getId(), t.getSchoolId(), t.getTrainingProgramId(), t.getStaffId(), t.isAttended(), t.getCertificateUrl()); }
    private AppraisalResponse map(AppraisalRecord a){ return new AppraisalResponse(a.getId(), a.getSchoolId(), a.getStaffId(), a.getAppraisalDate(), a.getAppraisalType(), a.getReviewerName(), a.getScore(), a.getSummary(), a.getRecommendations()); }
}
