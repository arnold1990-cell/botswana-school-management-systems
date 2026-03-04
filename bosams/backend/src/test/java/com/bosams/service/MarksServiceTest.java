package com.bosams.service;

import com.bosams.audit.AuditService;
import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.*;
import com.bosams.testutil.TestConstants;
import com.bosams.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarksServiceTest {
    @Mock ExamScheduleRepository schedules;
    @Mock MarkRepository marks;
    @Mock StudentRepository students;
    @Mock AuthorizationService auth;
    @Mock GradeProfileRepository gradeProfiles;
    @Mock AuditService audit;
    @InjectMocks MarksService service;

    private UserEntity teacher;
    private ExamSchedule schedule;

    @BeforeEach
    void setUp() {
        teacher = TestDataFactory.user(TestConstants.TEACHER_ID, Enums.Role.TEACHER);
        schedule = new ExamSchedule();
        schedule.setId(100L);
        schedule.setSubject(TestDataFactory.subject(9L, "Math"));
        schedule.setMarkEntryLastDate(LocalDate.now().plusDays(1));
        schedule.setExamDatetime(LocalDateTime.now());
        AcademicYear year = TestDataFactory.academicYear(1L, LocalDate.now().getYear(), true);
        ExamGroup group = new ExamGroup();
        group.setId(8L);
        group.setAcademicYear(year);
        group.setName("Midterm");
        schedule.setExamGroup(group);
        StandardEntity std = new StandardEntity(); std.setName("Grade 5");
        StreamEntity stream = new StreamEntity(); stream.setId(2L); stream.setStandard(std); stream.setName("A");
        schedule.setStream(stream);
        when(schedules.findById(100L)).thenReturn(Optional.of(schedule));
        when(auth.isTeacher(teacher)).thenReturn(true);
        when(auth.getActiveAcademicYear()).thenReturn(year);
        when(auth.teacherHasAssignment(teacher.getId(), 1L, 5, 9L)).thenReturn(true);
    }

    @Test
    void entryReturnsRows() {
        when(marks.findByExamScheduleId(100L)).thenReturn(List.of(new MarkEntity()));
        assertThat(service.entry(teacher, 100L)).hasSize(1);
    }

    @Test
    void saveDraftThrowsWhenLocked() {
        MarkEntity locked = new MarkEntity(); locked.setLocked(true);
        when(marks.findByExamScheduleId(100L)).thenReturn(List.of(locked));
        assertThatThrownBy(() -> service.saveDraft(teacher, 100L, List.of())).isInstanceOf(ApiException.class);
    }

    @Test
    void saveDraftCreatesMark() {
        StudentEntity s = TestDataFactory.learner(55L, 5);
        when(marks.findByExamScheduleId(100L)).thenReturn(List.of());
        when(students.findById(55L)).thenReturn(Optional.of(s));
        when(marks.findByExamScheduleIdAndStudentId(100L, 55L)).thenReturn(Optional.empty());

        service.saveDraft(teacher, 100L, List.of(new MarksService.MarkInput(55L, BigDecimal.valueOf(82), false)));

        verify(marks).save(any(MarkEntity.class));
    }

    @Test
    void submitLocksAllAndAudits() {
        when(marks.findByExamScheduleId(100L)).thenReturn(List.of(new MarkEntity()));

        service.submit(teacher, 100L, null);

        verify(marks).saveAll(anyList());
        verify(audit).log(eq(teacher.getId()), eq("MARKS_SUBMIT"), eq("mark"), eq("100"), isNull());
    }

    @Test
    void submitTeacherAfterDeadlineForbidden() {
        schedule.setMarkEntryLastDate(LocalDate.now().minusDays(1));
        assertThatThrownBy(() -> service.submit(teacher, 100L, null)).isInstanceOf(ApiException.class);
    }
}
