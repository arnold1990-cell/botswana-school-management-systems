package com.bosams;

import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.*;
import com.bosams.service.MarksService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class BosamsAuthorizationIntegrationTest {
    @Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource static void props(DynamicPropertyRegistry r){ r.add("spring.datasource.url", postgres::getJdbcUrl); r.add("spring.datasource.username", postgres::getUsername); r.add("spring.datasource.password", postgres::getPassword); }

    @Autowired UserRepository users;
    @Autowired ExamScheduleRepository schedules;
    @Autowired MarksService marksService;
    @Autowired ExamGroupRepository groups;
    @Autowired SubjectRepository subjects;
    @Autowired StreamRepository streams;
    @Autowired AcademicYearRepository years;

    @Test void teacherCannotAccessUnassignedSubject() {
        UserEntity teacher = users.findByEmail("teacher@bosams.local").orElseThrow();
        ExamSchedule schedule = new ExamSchedule();
        schedule.setExamGroup(groups.findById(1L).orElseThrow());
        schedule.setStream(streams.findById(1L).orElseThrow());
        schedule.setSubject(subjects.findById(2L).orElseThrow());
        schedule.setExamDatetime(LocalDateTime.now());
        schedule.setMarkEntryLastDate(LocalDate.now().plusDays(1));
        schedule.setMaxMarks(BigDecimal.valueOf(100));
        schedule.setStatus(Enums.EntityStatus.ACTIVE);
        schedule = schedules.save(schedule);
        assertThrows(ApiException.class, () -> marksService.entry(teacher, schedule.getId()));
    }

    @Test void teacherCannotAccessNonActiveYear() {
        UserEntity teacher = users.findByEmail("teacher@bosams.local").orElseThrow();
        ExamGroup g = new ExamGroup();
        g.setAcademicYear(years.findById(2L).orElseThrow());
        g.setName("Past Group");
        g.setStatus(Enums.ExamStatus.ACTIVE);
        g = groups.save(g);
        ExamSchedule s = new ExamSchedule();
        s.setExamGroup(g);
        s.setStream(streams.findById(1L).orElseThrow());
        s.setSubject(subjects.findById(1L).orElseThrow());
        s.setExamDatetime(LocalDateTime.now());
        s.setMarkEntryLastDate(LocalDate.now().plusDays(1));
        s.setMaxMarks(BigDecimal.valueOf(100));
        s.setStatus(Enums.EntityStatus.ACTIVE);
        s = schedules.save(s);
        assertThrows(ApiException.class, () -> marksService.entry(teacher, s.getId()));
    }

    @Test void lockPreventsEditing() {
        UserEntity teacher = users.findByEmail("teacher@bosams.local").orElseThrow();
        marksService.saveDraft(teacher,1L,List.of(new MarksService.MarkInput(1L, BigDecimal.valueOf(88), false)));
        marksService.submit(teacher,1L,null);
        ApiException ex = assertThrows(ApiException.class, () -> marksService.saveDraft(teacher,1L,List.of(new MarksService.MarkInput(1L, BigDecimal.valueOf(45), false))));
        assertEquals("LOCKED", ex.getCode());
    }

    @Test void principalCanOverrideDeadline() {
        UserEntity principal = users.findByEmail("principal@bosams.local").orElseThrow();
        ExamSchedule s = schedules.findById(1L).orElseThrow();
        s.setMarkEntryLastDate(LocalDate.now().minusDays(2)); schedules.save(s);
        assertDoesNotThrow(() -> marksService.submit(principal,1L,"Late submission approved"));
    }
}
