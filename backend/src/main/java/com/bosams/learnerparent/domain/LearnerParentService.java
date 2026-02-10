package com.bosams.learnerparent.domain;

import com.bosams.learnerparent.domain.enums.*;

import com.bosams.common.ConflictException;
import com.bosams.common.NotFoundException;
import com.bosams.learnerparent.dto.LearnerParentDtos.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LearnerParentService {
    private final LearnerApplicationRepository applications; private final LearnerRepository learners; private final LearnerTransferRepository transfers;
    private final ParentGuardianRepository parents; private final LearnerParentLinkRepository links; private final LearnerActivityMembershipRepository activities;
    private final LearnerTransportAssignmentRepository transports; private final LearnerLeadershipRoleRepository leadershipRoles; private final LearnerIncidentRepository incidents;
    private final LearnerLearningBarrierRepository barriers; private final LearnerMentorAssignmentRepository mentors; private final LearnerDisciplineEntryRepository discipline;
    private final LearnerDetentionActionRepository detentions; private final LearnerAttendanceRecordRepository attendance; private final LearnerAbsenceNotificationRepository notifications;

    public LearnerParentService(LearnerApplicationRepository applications, LearnerRepository learners, LearnerTransferRepository transfers, ParentGuardianRepository parents, LearnerParentLinkRepository links, LearnerActivityMembershipRepository activities, LearnerTransportAssignmentRepository transports, LearnerLeadershipRoleRepository leadershipRoles, LearnerIncidentRepository incidents, LearnerLearningBarrierRepository barriers, LearnerMentorAssignmentRepository mentors, LearnerDisciplineEntryRepository discipline, LearnerDetentionActionRepository detentions, LearnerAttendanceRecordRepository attendance, LearnerAbsenceNotificationRepository notifications) {this.applications = applications;this.learners = learners;this.transfers = transfers;this.parents = parents;this.links = links;this.activities = activities;this.transports = transports;this.leadershipRoles = leadershipRoles;this.incidents = incidents;this.barriers = barriers;this.mentors = mentors;this.discipline = discipline;this.detentions = detentions;this.attendance = attendance;this.notifications = notifications;}

    public LearnerApplication createApplication(ApplicationRequest r){ var e=new LearnerApplication(); e.setSchoolId(r.schoolId()); e.setApplicationNo(r.applicationNo()); e.setAppliedDate(Optional.ofNullable(r.appliedDate()).orElse(LocalDate.now())); e.setStatus(ApplicationStatus.SUBMITTED); e.setPreferredGradeId(r.preferredGradeId()); e.setPreferredAcademicYearId(r.preferredAcademicYearId()); e.setLearnerFirstName(r.learnerFirstName()); e.setLearnerLastName(r.learnerLastName()); e.setDateOfBirth(r.dateOfBirth()); e.setGender(r.gender()); e.setPreviousSchool(r.previousSchool()); e.setNotes(r.notes()); return applications.save(e);}
    public LearnerApplication getApplication(Long id){
        Optional<LearnerApplication> application = applications.findById(id);
        if (application.isEmpty()) {
            throw new NotFoundException("Application not found");
        }
        return application.get();
    }
    public Page<LearnerApplication> listApplications(Long schoolId, String status, LocalDate from, LocalDate to, Pageable p){ var f=Optional.ofNullable(from).orElse(LocalDate.of(1900,1,1)); var t=Optional.ofNullable(to).orElse(LocalDate.of(2999,1,1)); return status==null?applications.findBySchoolIdAndAppliedDateBetween(schoolId,f,t,p):applications.findBySchoolIdAndStatusAndAppliedDateBetween(schoolId,enumValue(ApplicationStatus.class, status),f,t,p);}
    public LearnerApplication decideApplication(Long id, String decision, DecisionRequest req){ var a=getApplication(id); if(!(a.getStatus()==ApplicationStatus.SUBMITTED || a.getStatus()==ApplicationStatus.UNDER_REVIEW)) throw new ConflictException("Application cannot transition from current status"); a.setStatus(enumValue(ApplicationStatus.class, decision)); a.setDecisionNote(req.decisionNote()); a.setDecidedBy(req.decidedBy()); a.setDecidedAt(Instant.now()); return applications.save(a);}

    public Learner createLearner(LearnerRequest r){ var l=new Learner(); apply(l,r); l.setStatus(LearnerStatus.ACTIVE); return learners.save(l);}
    public Learner updateLearner(Long id, LearnerRequest r){ var l=getLearner(id); apply(l,r); return learners.save(l);}
    public Learner getLearner(Long id){
        Optional<Learner> learner = learners.findById(id);
        if (learner.isEmpty()) {
            throw new NotFoundException("Learner not found");
        }
        return learner.get();
    }
    private void apply(Learner l, LearnerRequest r){ l.setSchoolId(r.schoolId()); l.setLearnerNo(r.learnerNo()); l.setFirstName(r.firstName()); l.setLastName(r.lastName()); l.setDateOfBirth(r.dateOfBirth()); l.setGender(r.gender()); l.setNationalId(r.nationalId()); l.setHomeLanguage(r.homeLanguage()); l.setCurrentAcademicYearId(r.currentAcademicYearId()); l.setCurrentGradeId(r.currentGradeId()); l.setCurrentClassRoomId(r.currentClassRoomId()); l.setHouseId(r.houseId()); l.setAdmissionDate(r.admissionDate()); }
    public Page<Learner> listLearners(Long schoolId,String status,Long y,Long g,Long c,Pageable p){ if(y!=null&&g!=null&&c!=null) return learners.findBySchoolIdAndCurrentAcademicYearIdAndCurrentGradeIdAndCurrentClassRoomId(schoolId,y,g,c,p); if(status!=null) return learners.findBySchoolIdAndStatus(schoolId,enumValue(LearnerStatus.class, status),p); return learners.findBySchoolId(schoolId,p);}
    public Learner archiveLearner(Long id, ArchiveRequest req){ var l=getLearner(id); if(l.getStatus()==LearnerStatus.ARCHIVED) throw new ConflictException("Learner already archived"); l.setStatus(LearnerStatus.ARCHIVED); l.setArchivedAt(Instant.now()); l.setArchiveReason(req.reason()); return learners.save(l);}
    public Learner restoreLearner(Long id){ var l=getLearner(id); if(l.getStatus()!=LearnerStatus.ARCHIVED) throw new ConflictException("Only archived learners can be restored"); l.setStatus(LearnerStatus.ACTIVE); l.setArchivedAt(null); l.setArchiveReason(null); return learners.save(l);}
    public Learner transferLearner(Long id,LearnerTransferRequest r){ var l=getLearner(id); var t=new LearnerTransfer(); t.setSchoolId(r.schoolId()); t.setLearnerId(id); t.setTransferType(enumValue(TransferType.class, r.transferType())); t.setFromAcademicYearId(l.getCurrentAcademicYearId()); t.setFromGradeId(l.getCurrentGradeId()); t.setFromClassRoomId(l.getCurrentClassRoomId()); t.setToAcademicYearId(r.toAcademicYearId()); t.setToGradeId(r.toGradeId()); t.setToClassRoomId(r.toClassRoomId()); t.setEffectiveDate(r.effectiveDate()); t.setReason(r.reason()); t.setCapturedBy(r.capturedBy()); t.setCapturedAt(Instant.now()); transfers.save(t); if(t.getTransferType()==TransferType.TRANSFER_OUT){l.setStatus(LearnerStatus.TRANSFERRED_OUT);} else { l.setCurrentAcademicYearId(r.toAcademicYearId()); l.setCurrentGradeId(r.toGradeId()); l.setCurrentClassRoomId(r.toClassRoomId()); } return learners.save(l);}

    public ParentGuardian createParent(ParentRequest r){ var p=new ParentGuardian(); apply(p,r); p.setStatus(ParentStatus.ACTIVE); return parents.save(p);} public ParentGuardian getParent(Long id){ Optional<ParentGuardian> parent = parents.findById(id); if(parent.isEmpty()) throw new NotFoundException("Parent not found"); return parent.get(); }
    public ParentGuardian updateParent(Long id, ParentRequest r){ var p=getParent(id); apply(p,r); return parents.save(p);} private void apply(ParentGuardian p, ParentRequest r){ p.setSchoolId(r.schoolId()); p.setParentNo(r.parentNo()); p.setFirstName(r.firstName()); p.setLastName(r.lastName()); p.setNationalId(r.nationalId()); p.setEmail(r.email()); p.setPhone(r.phone()); p.setAddress(r.address()); }
    public Page<ParentGuardian> listParents(Long schoolId, String status, Pageable pageable){ return parents.findBySchoolIdAndStatus(schoolId, enumValue(ParentStatus.class, Optional.ofNullable(status).orElse("ACTIVE")), pageable); }
    public ParentGuardian archiveParent(Long id, ArchiveRequest req){ var p=getParent(id); if(links.existsBySchoolIdAndParentIdAndIsPrimaryContactTrue(p.getSchoolId(), p.getId())) throw new ConflictException("Cannot archive parent while primary contact exists for active learner"); p.setStatus(ParentStatus.ARCHIVED); p.setArchivedAt(Instant.now()); p.setArchiveReason(req.reason()); return parents.save(p);}
    public ParentGuardian restoreParent(Long id){ var p=getParent(id); if(p.getStatus()!=ParentStatus.ARCHIVED) throw new ConflictException("Only archived parents can be restored"); p.setStatus(ParentStatus.ACTIVE); p.setArchivedAt(null); p.setArchiveReason(null); return parents.save(p);}
    public LearnerParentLink linkParent(Long learnerId, LinkParentRequest r){ var learner=getLearner(learnerId); var parent=getParent(r.parentId()); if(!Objects.equals(learner.getSchoolId(), r.schoolId())||!Objects.equals(parent.getSchoolId(), r.schoolId())) throw new ConflictException("Learner/parent school mismatch"); if(r.isPrimaryContact() && links.existsBySchoolIdAndLearnerIdAndIsPrimaryContactTrue(r.schoolId(), learnerId)) throw new ConflictException("Learner already has a primary contact"); var l=new LearnerParentLink(); l.setSchoolId(r.schoolId()); l.setLearnerId(learnerId); l.setParentId(r.parentId()); l.setRelationshipType(enumValue(RelationshipType.class, r.relationshipType())); l.setPrimaryContact(r.isPrimaryContact()); l.setLivesWithLearner(r.livesWithLearner()); return links.save(l);}
    public void unlinkParent(Long learnerId, Long parentId, Long schoolId){ links.deleteBySchoolIdAndLearnerIdAndParentId(schoolId, learnerId, parentId);}

    public Page<LearnerParentLink> learnerParents(Long schoolId, Long learnerId, Pageable p){ return links.findBySchoolIdAndLearnerId(schoolId, learnerId, p);} public Page<LearnerParentLink> parentLearners(Long schoolId, Long parentId, Pageable p){ return links.findBySchoolIdAndParentId(schoolId, parentId, p);}
    public Page<LearnerParentLink> primaryContacts(Long schoolId, Pageable p){ return links.findBySchoolIdAndIsPrimaryContactTrue(schoolId, p); }

    public LearnerActivityMembership addActivity(Long learnerId, ActivityRequest r){var e=new LearnerActivityMembership(); e.setSchoolId(r.schoolId()); e.setLearnerId(learnerId); e.setActivityType(enumValue(ActivityType.class, r.activityType())); e.setActivityId(r.activityId()); e.setRole(enumValue(ActivityRole.class, r.role())); e.setStartDate(r.startDate()); e.setEndDate(r.endDate()); return activities.save(e);} public Page<LearnerActivityMembership> listActivities(Long schoolId,Long learnerId,Pageable p){return activities.findBySchoolIdAndLearnerId(schoolId, learnerId,p);} public void deleteActivity(Long id){activities.deleteById(id);}
    public LearnerTransportAssignment addTransport(Long learnerId, TransportRequest r){var e=new LearnerTransportAssignment(); e.setSchoolId(r.schoolId()); e.setLearnerId(learnerId); e.setBusRouteId(r.busRouteId()); e.setTicketTypeId(r.ticketTypeId()); e.setStartDate(r.startDate()); e.setEndDate(r.endDate()); return transports.save(e);} public Page<LearnerTransportAssignment> listTransport(Long schoolId,Long learnerId,Pageable p){return transports.findBySchoolIdAndLearnerId(schoolId, learnerId,p);} public void deleteTransport(Long id){transports.deleteById(id);}
    public LearnerLeadershipRole addLeadership(Long learnerId, LeadershipRequest r){var e=new LearnerLeadershipRole(); e.setSchoolId(r.schoolId()); e.setLearnerId(learnerId); e.setRoleType(enumValue(LeadershipRoleType.class, r.roleType())); e.setAcademicYearId(r.academicYearId()); e.setStartDate(r.startDate()); e.setEndDate(r.endDate()); e.setNotes(r.notes()); return leadershipRoles.save(e);} public Page<LearnerLeadershipRole> listLeadership(Long schoolId,Long learnerId,Pageable p){return leadershipRoles.findBySchoolIdAndLearnerId(schoolId, learnerId,p);} public Page<LearnerLeadershipRole> listRcl(Long schoolId,Long yearId,Pageable p){return leadershipRoles.findBySchoolIdAndRoleTypeAndAcademicYearId(schoolId, LeadershipRoleType.RCL,yearId,p);}
    public LearnerIncident addIncident(Long learnerId, IncidentRequest r){var e=new LearnerIncident(); e.setSchoolId(r.schoolId()); e.setLearnerId(learnerId); e.setIncidentDate(r.incidentDate()); e.setCategory(enumValue(IncidentCategory.class, r.category())); e.setDescription(r.description()); e.setActionTaken(r.actionTaken()); e.setReportedBy(r.reportedBy()); return incidents.save(e);} public Page<LearnerIncident> listIncidents(Long schoolId,Long learnerId,Pageable p){return incidents.findBySchoolIdAndLearnerId(schoolId, learnerId,p);}
    public LearnerLearningBarrier addBarrier(Long learnerId, BarrierRequest r){var e=new LearnerLearningBarrier(); e.setSchoolId(r.schoolId()); e.setLearnerId(learnerId); e.setBarrierType(enumValue(BarrierType.class, r.barrierType())); e.setNotes(r.notes()); e.setIdentifiedDate(r.identifiedDate()); return barriers.save(e);} public Page<LearnerLearningBarrier> listBarriers(Long schoolId,Long learnerId,Pageable p){return barriers.findBySchoolIdAndLearnerId(schoolId, learnerId,p);}
    public LearnerMentorAssignment addMentor(Long learnerId, MentorRequest r){var e=new LearnerMentorAssignment(); e.setSchoolId(r.schoolId()); e.setLearnerId(learnerId); e.setMentorStaffId(r.mentorStaffId()); e.setStartDate(r.startDate()); e.setEndDate(r.endDate()); e.setNotes(r.notes()); return mentors.save(e);} public Page<LearnerMentorAssignment> listMentors(Long schoolId,Long learnerId,Pageable p){return mentors.findBySchoolIdAndLearnerId(schoolId, learnerId,p);}
    public LearnerDisciplineEntry addDiscipline(Long learnerId, DisciplineRequest r){var e=new LearnerDisciplineEntry(); e.setSchoolId(r.schoolId()); e.setLearnerId(learnerId); e.setEntryDate(r.entryDate()); e.setEntryType(enumValue(DisciplineEntryType.class, r.entryType())); e.setCodeId(r.codeId()); e.setPoints(r.points()); e.setNotes(r.notes()); e.setCapturedBy(r.capturedBy()); return discipline.save(e);} public Page<LearnerDisciplineEntry> listDiscipline(Long schoolId,Long learnerId,LocalDate from,LocalDate to,Pageable p){ return discipline.findBySchoolIdAndLearnerIdAndEntryDateBetween(schoolId,learnerId,from,to,p);}
    public LearnerDetentionAction addDetention(Long learnerId, DetentionRequest r){var e=new LearnerDetentionAction(); e.setSchoolId(r.schoolId()); e.setLearnerId(learnerId); e.setScheduledDate(r.scheduledDate()); e.setDurationMinutes(r.durationMinutes()); e.setReason(r.reason()); e.setStatus(enumValue(DetentionStatus.class, r.status())); return detentions.save(e);} public Page<LearnerDetentionAction> listDetentions(Long schoolId,Long learnerId,Pageable p){return detentions.findBySchoolIdAndLearnerId(schoolId, learnerId,p);}
    public List<LearnerAttendanceRecord> captureAttendance(AttendanceCaptureRequest r){ List<LearnerAttendanceRecord> out=new ArrayList<>(); var seenLearners=new HashSet<Long>(); for(var item:r.records()){ if(!seenLearners.add(item.learnerId())) throw new ConflictException("Duplicate learner attendance in same request"); if(attendance.existsBySchoolIdAndLearnerIdAndDateAndPeriod(r.schoolId(), item.learnerId(), r.date(), r.period())) throw new ConflictException("Attendance already captured for learner/date/period"); var e=new LearnerAttendanceRecord(); e.setSchoolId(r.schoolId()); e.setLearnerId(item.learnerId()); e.setAcademicYearId(r.academicYearId()); e.setTermId(r.termId()); e.setDate(r.date()); e.setPeriod(r.period()); e.setStatus(enumValue(AttendanceStatus.class, item.status())); e.setNotes(item.notes()); e.setCapturedBy(item.capturedBy()); e.setCapturedAt(Instant.now()); out.add(attendance.save(e)); } return out; }
    public Page<LearnerAttendanceRecord> attendanceByClass(Long schoolId, LocalDate date, Long classRoomId, Integer period, Pageable p){
        LocalDate effectiveDate = date == null ? LocalDate.now() : date;
        if(classRoomId==null) return attendance.findBySchoolIdAndDateAndPeriod(schoolId,effectiveDate,period,p);
        List<Learner> classLearners = learners.findBySchoolIdAndCurrentClassRoomId(schoolId,classRoomId,Pageable.unpaged()).getContent();
        List<Long> ids = new ArrayList<>();
        for (Learner learner : classLearners) {
            ids.add(learner.getId());
        }
        if(ids.isEmpty()) return Page.empty(p);
        return attendance.findBySchoolIdAndDateAndPeriodAndLearnerIdIn(schoolId,effectiveDate,period,ids,p);
    }
    public Page<LearnerAttendanceRecord> learnerAttendance(Long schoolId, Long learnerId, LocalDate from, LocalDate to, Pageable p){ return attendance.findBySchoolIdAndLearnerIdAndDateBetween(schoolId, learnerId, from, to, p);}
    public LearnerAbsenceNotification notifyAbsence(Long learnerId, AbsenceNotifyRequest r){ var e=new LearnerAbsenceNotification(); e.setSchoolId(r.schoolId()); e.setLearnerId(learnerId); e.setDate(r.date()); e.setChannel(enumValue(NotificationChannel.class, r.channel())); e.setMessage(r.message()); e.setStatus(NotificationStatus.PENDING); return notifications.save(e);}

    public Map<String,Object> disciplineSummary(Long schoolId, LocalDate from, LocalDate to){
        LocalDate effectiveFrom = from == null ? LocalDate.of(1900,1,1) : from;
        LocalDate effectiveTo = to == null ? LocalDate.of(2999,1,1) : to;
        List<LearnerDisciplineEntry> entries = discipline.findBySchoolIdAndEntryDateBetween(schoolId,effectiveFrom,effectiveTo,Pageable.unpaged()).getContent();
        Map<Long,Integer> learnerPoints = new HashMap<>();
        Map<String,Integer> dist = new HashMap<>();
        for (LearnerDisciplineEntry entry : entries) {
            Integer currentPoints = learnerPoints.get(entry.getLearnerId());
            if (currentPoints == null) {
                learnerPoints.put(entry.getLearnerId(), entry.getPoints());
            } else {
                learnerPoints.put(entry.getLearnerId(), currentPoints + entry.getPoints());
            }

            String key = entry.getEntryType().name();
            Integer currentCount = dist.get(key);
            if (currentCount == null) {
                dist.put(key, 1);
            } else {
                dist.put(key, currentCount + 1);
            }
        }

        List<Map.Entry<Long,Integer>> sortedEntries = new ArrayList<>(learnerPoints.entrySet());
        sortedEntries.sort(Map.Entry.<Long,Integer>comparingByValue().reversed());
        List<Map<String,Object>> top = new ArrayList<>();
        int limit = Math.min(5, sortedEntries.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<Long,Integer> topEntry = sortedEntries.get(i);
            Map<String,Object> topItem = new HashMap<>();
            topItem.put("learnerId", topEntry.getKey());
            topItem.put("points", topEntry.getValue());
            top.add(topItem);
        }

        Map<String,Object> result = new HashMap<>();
        result.put("pointsDistribution", dist);
        result.put("topOffenders", top);
        result.put("totalEntries", entries.size());
        result.put("totalsPerGradeClass", new ArrayList<Map<String,Object>>());
        return result;
    }
    public Map<String,Object> attendanceSummary(Long schoolId, LocalDate from, LocalDate to){
        LocalDate effectiveFrom = from == null ? LocalDate.of(1900,1,1) : from;
        LocalDate effectiveTo = to == null ? LocalDate.of(2999,1,1) : to;
        List<LearnerAttendanceRecord> rec = attendance.findBySchoolIdAndDateBetween(schoolId,effectiveFrom,effectiveTo,Pageable.unpaged()).getContent();
        long total = rec.size();
        long absent = 0;
        long late = 0;
        long excused = 0;
        for (LearnerAttendanceRecord attendanceRecord : rec) {
            if (attendanceRecord.getStatus() == AttendanceStatus.ABSENT) {
                absent++;
            }
            if (attendanceRecord.getStatus() == AttendanceStatus.LATE) {
                late++;
            }
            if (attendanceRecord.getStatus() == AttendanceStatus.EXCUSED) {
                excused++;
            }
        }
        double rate=total==0?0:((double)(total-absent)/total);
        Map<String,Object> result = new HashMap<>();
        result.put("attendanceRate", rate);
        result.put("absentCount", absent);
        result.put("lateCount", late);
        result.put("excusedCount", excused);
        result.put("topAbsentLearners", new ArrayList<Map<String,Object>>());
        return result;
    }
    public Map<String,Object> enrolment(Long schoolId, Long yearId){
        List<Learner> learnerList = learners.findBySchoolId(schoolId, Pageable.unpaged()).getContent();
        List<Learner> all = new ArrayList<>();
        for (Learner learner : learnerList) {
            if (Objects.equals(learner.getCurrentAcademicYearId(),yearId)) {
                all.add(learner);
            }
        }
        Map<String,Long> by= new HashMap<>();
        for (Learner learner : all) {
            String key = learner.getCurrentGradeId()+":"+learner.getCurrentClassRoomId();
            Long current = by.get(key);
            if (current == null) {
                by.put(key, 1L);
            } else {
                by.put(key, current + 1L);
            }
        }
        Map<String,Object> result = new HashMap<>();
        result.put("grandTotal",all.size());
        result.put("totals",by);
        return result;
    }
    public List<Map<String,Object>> ageList(Long schoolId, Long yearId, Long gradeId, LocalDate ref){
        LocalDate effectiveRef = ref == null ? LocalDate.now() : ref;
        List<Learner> learnerList = learners.findBySchoolId(schoolId,Pageable.unpaged()).getContent();
        List<Map<String,Object>> result = new ArrayList<>();
        for (Learner learner : learnerList) {
            if (Objects.equals(learner.getCurrentAcademicYearId(),yearId)&&Objects.equals(learner.getCurrentGradeId(),gradeId)) {
                Map<String,Object> item = new HashMap<String, Object>();
                item.put("learnerId",learner.getId());
                item.put("learnerNo",learner.getLearnerNo());
                Integer age = null;
                if (learner.getDateOfBirth() != null) {
                    age = Period.between(learner.getDateOfBirth(),effectiveRef).getYears();
                }
                item.put("age",age);
                result.add(item);
            }
        }
        return result;
    }
    public Page<LearnerActivityMembership> activityMembers(Long schoolId, String type, Long activityId, Pageable p){ return activities.findBySchoolIdAndActivityTypeAndActivityId(schoolId, enumValue(ActivityType.class, type), activityId, p);}
    public List<Map<String,Object>> aggregateLearners(Long schoolId, Long yearId){
        List<Learner> learnerList = learners.findBySchoolId(schoolId,Pageable.unpaged()).getContent();
        List<Map<String,Object>> result = new ArrayList<>();
        for (Learner learner : learnerList) {
            if (Objects.equals(learner.getCurrentAcademicYearId(),yearId)) {
                Map<String,Object> item = new HashMap<String, Object>();
                item.put("learnerId",learner.getId());
                item.put("learnerNo",learner.getLearnerNo());
                item.put("name",learner.getFirstName()+" "+learner.getLastName());
                item.put("status",learner.getStatus().name());
                item.put("gradeId",learner.getCurrentGradeId());
                item.put("classRoomId",learner.getCurrentClassRoomId());
                result.add(item);
            }
        }
        return result;
    }
    private <E extends Enum<E>> E enumValue(Class<E> enumClass, String raw){ try{ return Enum.valueOf(enumClass, raw);} catch(Exception ex){ throw new IllegalArgumentException("Invalid value '"+raw+"' for "+enumClass.getSimpleName()); } }
}
