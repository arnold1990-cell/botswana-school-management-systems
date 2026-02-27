package com.bosams.web;

import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/teachers")
public class TeacherAssignmentController {
    private final UserRepository users; private final TeacherAssignmentRepository assignments; private final AcademicYearRepository years; private final StreamRepository streams; private final SubjectRepository subjects;
    public TeacherAssignmentController(UserRepository users, TeacherAssignmentRepository assignments, AcademicYearRepository years, StreamRepository streams, SubjectRepository subjects) {this.users=users;this.assignments=assignments;this.years=years;this.streams=streams;this.subjects=subjects;}
    @GetMapping public List<UserEntity> teachers(@AuthenticationPrincipal UserEntity me){ managerOnly(me); return users.findByRole(Enums.Role.TEACHER); }
    @GetMapping("/{teacherUserId}/assignments") public List<TeacherAssignment> list(@AuthenticationPrincipal UserEntity me, @PathVariable Long teacherUserId, @RequestParam Long academicYearId){ managerOnly(me); return assignments.findByTeacherIdAndAcademicYearIdAndActiveTrue(teacherUserId, academicYearId); }
    @PostMapping("/{teacherUserId}/assignments") public TeacherAssignment create(@AuthenticationPrincipal UserEntity me,@PathVariable Long teacherUserId,@RequestBody AssignReq req){ managerOnly(me); TeacherAssignment ta=new TeacherAssignment(); ta.setTeacher(users.findById(teacherUserId).orElseThrow()); ta.setAcademicYear(years.findById(req.academicYearId()).orElseThrow()); ta.setStream(streams.findById(req.streamId()).orElseThrow()); ta.setSubject(subjects.findById(req.subjectId()).orElseThrow()); return assignments.save(ta); }
    @PatchMapping("/assignments/{assignmentId}/active") public TeacherAssignment toggle(@AuthenticationPrincipal UserEntity me,@PathVariable Long assignmentId,@RequestBody ToggleReq req){ managerOnly(me); TeacherAssignment t=assignments.findById(assignmentId).orElseThrow(); t.setActive(req.active()); return assignments.save(t); }
    @DeleteMapping("/assignments/{assignmentId}") public void delete(@AuthenticationPrincipal UserEntity me,@PathVariable Long assignmentId){ managerOnly(me); assignments.deleteById(assignmentId); }
    private void managerOnly(UserEntity me){ if(me.getRole()==Enums.Role.TEACHER) throw new ApiException(HttpStatus.FORBIDDEN,"FORBIDDEN","Forbidden"); }
    public record AssignReq(Long academicYearId,Long streamId,Long subjectId){}
    public record ToggleReq(boolean active){}
}
