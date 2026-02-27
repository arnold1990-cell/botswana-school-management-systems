package com.bosams.service;

import com.bosams.audit.AuditService;
import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
public class MarksService {
    private final ExamScheduleRepository schedules; private final MarkRepository marks; private final StudentRepository students; private final AuthorizationService authz; private final GradeProfileRepository gradeProfiles; private final AuditService audit;
    public MarksService(ExamScheduleRepository schedules, MarkRepository marks, StudentRepository students, AuthorizationService authz, GradeProfileRepository gradeProfiles, AuditService audit) {this.schedules=schedules;this.marks=marks;this.students=students;this.authz=authz;this.gradeProfiles=gradeProfiles;this.audit=audit;}

    public List<MarkEntity> entry(UserEntity actor, Long scheduleId){
        ExamSchedule s = getSched(scheduleId); enforceScheduleAccess(actor, s);
        return marks.findByExamScheduleId(scheduleId);
    }

    @Transactional
    public void saveDraft(UserEntity actor, Long scheduleId, List<MarkInput> inputs){
        ExamSchedule s = getSched(scheduleId); enforceScheduleAccess(actor, s);
        for (MarkEntity existing : marks.findByExamScheduleId(scheduleId)) if(existing.isLocked()) throw new ApiException(HttpStatus.CONFLICT,"LOCKED","Marks already locked");
        for (MarkInput in : inputs) {
            StudentEntity st = students.findById(in.studentId()).orElseThrow();
            MarkEntity m = marks.findByExamScheduleIdAndStudentId(scheduleId, st.getId()).orElseGet(MarkEntity::new);
            m.setExamSchedule(s); m.setStudent(st); apply(m, in.marks(), in.absent()); marks.save(m);
        }
    }

    @Transactional
    public void submit(UserEntity actor, Long scheduleId, String overrideReason){
        ExamSchedule s = getSched(scheduleId); enforceScheduleAccess(actor, s);
        boolean afterDeadline = LocalDate.now().isAfter(s.getMarkEntryLastDate());
        if (authz.isTeacher(actor) && afterDeadline) throw new ApiException(HttpStatus.FORBIDDEN,"DEADLINE_PASSED","Deadline passed");
        if ((authz.isAdmin(actor)||authz.isPrincipal(actor)) && afterDeadline && (overrideReason==null || overrideReason.isBlank())) throw new ApiException(HttpStatus.CONFLICT,"OVERRIDE_REASON_REQUIRED","overrideReason is required");
        List<MarkEntity> all = marks.findByExamScheduleId(scheduleId);
        all.forEach(m->{m.setLocked(true);m.setLockedAt(Instant.now());}); marks.saveAll(all);
        audit.log(actor.getId(),"MARKS_SUBMIT","mark",String.valueOf(scheduleId),overrideReason);
    }

    private void enforceScheduleAccess(UserEntity actor, ExamSchedule s){
        if (authz.isTeacher(actor)) {
            AcademicYear active = authz.getActiveAcademicYear();
            if (!s.getExamGroup().getAcademicYear().getId().equals(active.getId())) throw new ApiException(HttpStatus.FORBIDDEN,"NON_ACTIVE_YEAR","Teachers can only access active year");
            if (!authz.teacherHasAssignment(actor.getId(), active.getId(), s.getStream().getId(), s.getSubject().getId())) throw new ApiException(HttpStatus.FORBIDDEN,"NOT_ASSIGNED","Teacher not assigned");
        }
    }
    private ExamSchedule getSched(Long id){ return schedules.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"NOT_FOUND","Schedule not found")); }
    private void apply(MarkEntity m, BigDecimal marks, boolean absent){
        if(absent){ m.setAbsent(true); m.setMarks(null); m.setGrade("-"); m.setResult(Enums.MarkResult.ABSENT); return; }
        m.setAbsent(false); m.setMarks(marks); String grade=gradeFrom(marks); m.setGrade(grade); m.setResult(("F".equals(grade))?Enums.MarkResult.FAIL:Enums.MarkResult.PASS);
    }
    private String gradeFrom(BigDecimal v){ if(v==null)return "F"; if(v.compareTo(BigDecimal.valueOf(80))>=0) return "A"; if(v.compareTo(BigDecimal.valueOf(70))>=0) return "B"; if(v.compareTo(BigDecimal.valueOf(60))>=0) return "C"; if(v.compareTo(BigDecimal.valueOf(50))>=0) return "D"; return "F"; }

    public record MarkInput(Long studentId, BigDecimal marks, boolean absent) {}
}
