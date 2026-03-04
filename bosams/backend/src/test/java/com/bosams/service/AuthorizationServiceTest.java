package com.bosams.service;

import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.AcademicYearRepository;
import com.bosams.repository.TeacherAssignmentRepository;
import com.bosams.testutil.TestConstants;
import com.bosams.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {
    @Mock AcademicYearRepository yearRepository;
    @Mock TeacherAssignmentRepository assignmentRepository;
    @InjectMocks AuthorizationService service;

    @Test
    void roleChecksWork() {
        assertThat(service.isAdmin(TestDataFactory.user(TestConstants.USER_ID, Enums.Role.ADMIN))).isTrue();
        assertThat(service.isTeacher(TestDataFactory.user(TestConstants.TEACHER_ID, Enums.Role.TEACHER))).isTrue();
        assertThat(service.isPrincipal(TestDataFactory.user(TestConstants.USER_ID, Enums.Role.PRINCIPAL))).isTrue();
    }

    @Test
    void getActiveAcademicYearThrowsWhenMissing() {
        when(yearRepository.findByActiveTrue()).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getActiveAcademicYear()).isInstanceOf(ApiException.class);
    }

    @Test
    void teacherGradeLevelsReturnsSet() {
        TeacherAssignment a = new TeacherAssignment();
        a.setGradeLevel(5);
        when(assignmentRepository.findByTeacherIdAndAcademicYearIdAndActiveTrue(TestConstants.TEACHER_ID, 1L)).thenReturn(List.of(a));

        assertThat(service.teacherGradeLevels(TestConstants.TEACHER_ID, 1L)).containsExactly(5);
    }

    @Test
    void enforceTeacherAssignmentThrowsWhenNotAssigned() {
        UserEntity teacher = TestDataFactory.user(TestConstants.TEACHER_ID, Enums.Role.TEACHER);
        AcademicYear year = TestDataFactory.academicYear(1L, 2024, true);
        when(yearRepository.findByActiveTrue()).thenReturn(Optional.of(year));
        when(assignmentRepository.existsByTeacherIdAndAcademicYearIdAndGradeLevelAndSubjectIdAndActiveTrue(any(), anyLong(), anyInt(), anyLong())).thenReturn(false);

        assertThatThrownBy(() -> service.enforceTeacherAssignment(teacher, 4, 9L)).isInstanceOf(ApiException.class);
    }

    @Test
    void teacherStreamAndSubjectNullsReturnEmpty() {
        assertThat(service.teacherStreamIds(null, 1L)).isEmpty();
        assertThat(service.teacherSubjectIdsForStream(TestConstants.TEACHER_ID, 1L, null)).isEmpty();
        verifyNoInteractions(assignmentRepository);
    }
}
