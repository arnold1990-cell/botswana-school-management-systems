package com.bosams.web;

import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.*;
import com.bosams.service.AuthorizationService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController @RequestMapping("/api/reports")
public class ReportsController {
    private final StudentRepository students; private final ExamScheduleRepository schedules; private final MarkRepository marks; private final AuthorizationService authz;
    public ReportsController(StudentRepository students, ExamScheduleRepository schedules, MarkRepository marks, AuthorizationService authz) {this.students=students;this.schedules=schedules;this.marks=marks;this.authz=authz;}

    @GetMapping("/student")
    public Map<String,Object> student(@AuthenticationPrincipal UserEntity me, @RequestParam Long studentId, @RequestParam Long examGroupId){
        StudentEntity s = students.findById(studentId).orElseThrow();
        enforceTeacherStream(me, s.getStream().getId());
        List<MarkEntity> rows = marks.findAll().stream().filter(m->m.getStudent().getId().equals(studentId) && m.getExamSchedule().getExamGroup().getId().equals(examGroupId)).toList();
        return Map.of("student", s, "marks", rows);
    }
    @GetMapping("/subject")
    public List<MarkEntity> subject(@AuthenticationPrincipal UserEntity me,@RequestParam Long streamId,@RequestParam Long subjectId,@RequestParam Long examGroupId){
        enforceTeacherSubject(me, streamId, subjectId);
        return marks.findAll().stream().filter(m->m.getExamSchedule().getExamGroup().getId().equals(examGroupId) && m.getExamSchedule().getStream().getId().equals(streamId) && m.getExamSchedule().getSubject().getId().equals(subjectId)).toList();
    }
    @GetMapping("/consolidated")
    public List<MarkEntity> consolidated(@AuthenticationPrincipal UserEntity me,@RequestParam Long streamId,@RequestParam Long examGroupId,@RequestParam(defaultValue = "true") boolean rank){
        enforceTeacherStream(me, streamId);
        Set<Long> allowedSubjects = me.getRole()==Enums.Role.TEACHER ? authz.teacherSubjectIdsForStream(me.getId(), authz.getActiveAcademicYear().getId(), streamId) : Set.of();
        return marks.findAll().stream().filter(m->m.getExamSchedule().getExamGroup().getId().equals(examGroupId) && m.getExamSchedule().getStream().getId().equals(streamId) && (me.getRole()!=Enums.Role.TEACHER || allowedSubjects.contains(m.getExamSchedule().getSubject().getId()))).toList();
    }

    @GetMapping(value = "/student/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> studentPdf(@AuthenticationPrincipal UserEntity me,@RequestParam Long studentId,@RequestParam Long examGroupId){ student(me,studentId,examGroupId); return pdf("Student report"); }
    @GetMapping(value = "/subject/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> subjectPdf(@AuthenticationPrincipal UserEntity me,@RequestParam Long streamId,@RequestParam Long subjectId,@RequestParam Long examGroupId){ subject(me,streamId,subjectId,examGroupId); return pdf("Subject report"); }
    @GetMapping(value = "/consolidated/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> consolidatedPdf(@AuthenticationPrincipal UserEntity me,@RequestParam Long streamId,@RequestParam Long examGroupId){ consolidated(me,streamId,examGroupId,true); return pdf("Consolidated report"); }

    private ResponseEntity<byte[]> pdf(String title){ return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=report.pdf").body(("%PDF-1.4\n"+title).getBytes(StandardCharsets.UTF_8)); }
    private void enforceTeacherStream(UserEntity me, Long streamId){ if(me.getRole()==Enums.Role.TEACHER){ AcademicYear ay=authz.getActiveAcademicYear(); if(!authz.teacherStreamIds(me.getId(), ay.getId()).contains(streamId)) throw new ApiException(HttpStatus.FORBIDDEN,"NOT_ASSIGNED","Not assigned stream"); }}
    private void enforceTeacherSubject(UserEntity me, Long streamId, Long subjectId){ enforceTeacherStream(me,streamId); if(me.getRole()==Enums.Role.TEACHER){ AcademicYear ay=authz.getActiveAcademicYear(); if(!authz.teacherSubjectIdsForStream(me.getId(), ay.getId(), streamId).contains(subjectId)) throw new ApiException(HttpStatus.FORBIDDEN,"NOT_ASSIGNED","Not assigned subject"); }}
}
