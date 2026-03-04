package com.bosams.service;

import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.*;
import com.bosams.testutil.TestConstants;
import com.bosams.testutil.TestDataFactory;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcademicMarksServiceTest {
    @Mock StudentRepository learners;
    @Mock SubjectRepository subjects;
    @Mock AssessmentTaskRepository tasks;
    @Mock MarkEntryRepository markEntries;
    @Mock GradeCalculationService gradeCalculationService;
    @Mock UserRepository users;
    @Mock AuthorizationService authorizationService;
    @InjectMocks AcademicMarksService service;

    private UserEntity teacher;

    @BeforeEach
    void init() {
        teacher = TestDataFactory.user(TestConstants.TEACHER_ID, Enums.Role.TEACHER);
        when(users.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(subjects.findById(9L)).thenReturn(Optional.of(TestDataFactory.subject(9L, "Math")));
        AssessmentTaskEntity task = new AssessmentTaskEntity(); task.setId(7L);
        when(tasks.findById(7L)).thenReturn(Optional.of(task));
        when(gradeCalculationService.calculate(40)).thenReturn(Enums.GradeLetter.A);
    }

    @Test
    void bulkSaveHappyPath() {
        StudentEntity learner = TestDataFactory.learner(10L, 5);
        when(learners.findById(10L)).thenReturn(Optional.of(learner));
        when(authorizationService.isTeacher(teacher)).thenReturn(false);
        when(markEntries.findByLearnerIdAndSubjectIdAndTaskId(10L, 9L, 7L)).thenReturn(Optional.empty());
        when(markEntries.save(any())).thenAnswer(i -> i.getArgument(0));

        var out = service.bulkSave(teacher.getId(), new AcademicMarksService.BulkMarkRequest(9L, 7L, List.of(new AcademicMarksService.BulkLearnerMark(10L, 40))));

        assertThat(out).hasSize(1);
        verify(markEntries).save(any(MarkEntryEntity.class));
    }

    @Test
    void bulkSaveValidationScoreOutOfRange() {
        assertThatThrownBy(() -> service.bulkSave(teacher.getId(), new AcademicMarksService.BulkMarkRequest(9L, 7L, List.of(new AcademicMarksService.BulkLearnerMark(10L, 99)))))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void bulkSaveThrowsWhenSubmittedLocked() {
        StudentEntity learner = TestDataFactory.learner(10L, 5);
        MarkEntryEntity locked = new MarkEntryEntity();
        locked.setId(1L);
        locked.setStatus(Enums.MarkEntryStatus.SUBMITTED);
        when(learners.findById(10L)).thenReturn(Optional.of(learner));
        when(authorizationService.isTeacher(teacher)).thenReturn(false);
        when(markEntries.findByLearnerIdAndSubjectIdAndTaskId(10L, 9L, 7L)).thenReturn(Optional.of(locked));

        assertThatThrownBy(() -> service.bulkSave(teacher.getId(), new AcademicMarksService.BulkMarkRequest(9L, 7L, List.of(new AcademicMarksService.BulkLearnerMark(10L, 40)))))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void submitThrowsForNonTeacher() {
        when(authorizationService.isTeacher(teacher)).thenReturn(false);
        assertThatThrownBy(() -> service.submit(teacher.getId(), 9L, 7L, 5)).isInstanceOf(ApiException.class);
    }

    @Test
    void statusReturnsSubmittedWhenAnySubmitted() {
        StudentEntity learner = TestDataFactory.learner(10L, 5);
        MarkEntryEntity e = new MarkEntryEntity(); e.setLearner(learner); e.setStatus(Enums.MarkEntryStatus.SUBMITTED);
        when(markEntries.findByTaskIdAndSubjectId(7L, 9L)).thenReturn(List.of(e));

        var out = service.status(9L, 7L, 5);

        assertThat(out.status()).isEqualTo("SUBMITTED");
        assertThat(out.rows()).isEqualTo(1);
    }
}
