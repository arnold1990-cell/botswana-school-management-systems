package com.bosams.web;

import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.*;
import com.bosams.service.AuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class ExamController {
    private final ExamScheduleRepository schedules; private final ExamGroupRepository groups; private final AuthorizationService authz;
    public ExamController(ExamScheduleRepository schedules, ExamGroupRepository groups, AuthorizationService authz) {this.schedules=schedules;this.groups=groups;this.authz=authz;}
    @GetMapping("/api/exam-schedules") public List<ExamSchedule> list(@AuthenticationPrincipal UserEntity me,@RequestParam(required = false) Long streamId){
        if(me.getRole()==Enums.Role.TEACHER){ AcademicYear ay=authz.getActiveAcademicYear(); if(streamId!=null && !authz.teacherStreamIds(me.getId(), ay.getId()).contains(streamId)) throw new ApiException(HttpStatus.FORBIDDEN,"NOT_ASSIGNED","Not assigned");
            if(streamId==null){ List<ExamSchedule> all=schedules.findAll(); Set<Long> streams=authz.teacherStreamIds(me.getId(), ay.getId()); return all.stream().filter(s->streams.contains(s.getStream().getId())&&s.getExamGroup().getAcademicYear().getId().equals(ay.getId())).toList(); }
        }
        return streamId==null?schedules.findAll():schedules.findByStreamId(streamId);
    }
    @GetMapping("/api/exam-schedules/{id}") public ExamSchedule get(@AuthenticationPrincipal UserEntity me,@PathVariable Long id){ return list(me,null).stream().filter(s->s.getId().equals(id)).findFirst().orElseThrow(()->new ApiException(HttpStatus.FORBIDDEN,"FORBIDDEN","Forbidden")); }
    @PostMapping("/api/exam-groups") public ExamGroup createGroup(@AuthenticationPrincipal UserEntity me,@RequestBody ExamGroup req){ if(me.getRole()==Enums.Role.TEACHER) throw new ApiException(HttpStatus.FORBIDDEN,"FORBIDDEN","Forbidden"); req.setId(null); return groups.save(req); }
}
