package com.bosams.learnerparent.domain;

import com.bosams.common.ConflictException;
import com.bosams.learnerparent.dto.LearnerParentDtos.ArchiveRequest;
import com.bosams.learnerparent.dto.LearnerParentDtos.AttendanceCaptureRequest;
import com.bosams.learnerparent.dto.LearnerParentDtos.AttendanceItem;
import com.bosams.learnerparent.dto.LearnerParentDtos.LinkParentRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LearnerParentServiceTest {

    @Mock LearnerApplicationRepository applications;
    @Mock LearnerRepository learners;
    @Mock LearnerTransferRepository transfers;
    @Mock ParentGuardianRepository parents;
    @Mock LearnerParentLinkRepository links;
    @Mock LearnerActivityMembershipRepository activities;
    @Mock LearnerTransportAssignmentRepository transports;
    @Mock LearnerLeadershipRoleRepository leadershipRoles;
    @Mock LearnerIncidentRepository incidents;
    @Mock LearnerLearningBarrierRepository barriers;
    @Mock LearnerMentorAssignmentRepository mentors;
    @Mock LearnerDisciplineEntryRepository discipline;
    @Mock LearnerDetentionActionRepository detentions;
    @Mock LearnerAttendanceRecordRepository attendance;
    @Mock LearnerAbsenceNotificationRepository notifications;

    LearnerParentService service;

    @BeforeEach
    void setUp() {
        service = new LearnerParentService(applications, learners, transfers, parents, links, activities, transports,
                leadershipRoles, incidents, barriers, mentors, discipline, detentions, attendance, notifications);
    }

    @Test
    void archiveLearnerThrowsWhenAlreadyArchived() {
        var learner = new Learner();
        learner.setStatus(LearnerStatus.ARCHIVED);
        when(learners.findById(9L)).thenReturn(Optional.of(learner));

        assertThrows(ConflictException.class, () -> service.archiveLearner(9L, new ArchiveRequest("duplicate")));
        verify(learners, never()).save(any());
    }

    @Test
    void linkParentRejectsCrossSchoolLinking() {
        var learner = new Learner();
        learner.setSchoolId(1L);
        var parent = new ParentGuardian();
        parent.setSchoolId(2L);
        when(learners.findById(3L)).thenReturn(Optional.of(learner));
        when(parents.findById(4L)).thenReturn(Optional.of(parent));

        assertThrows(ConflictException.class,
                () -> service.linkParent(3L, new LinkParentRequest(1L, 4L, "MOTHER", true, true)));
    }

    @Test
    void captureAttendanceRejectsDuplicateLearnerInRequest() {
        var request = new AttendanceCaptureRequest(1L, 1L, 1L, LocalDate.of(2025, 1, 10), 1, null,
                List.of(new AttendanceItem(7L, "PRESENT", null, "t"), new AttendanceItem(7L, "ABSENT", null, "t")));

        assertThrows(ConflictException.class, () -> service.captureAttendance(request));
        verify(attendance, never()).save(any());
    }

    @Test
    void captureAttendanceRejectsAlreadyCapturedRecord() {
        when(attendance.existsBySchoolIdAndLearnerIdAndDateAndPeriod(1L, 7L, LocalDate.of(2025, 1, 10), 1)).thenReturn(true);
        var request = new AttendanceCaptureRequest(1L, 1L, 1L, LocalDate.of(2025, 1, 10), 1, null,
                List.of(new AttendanceItem(7L, "PRESENT", null, "t")));

        assertThrows(ConflictException.class, () -> service.captureAttendance(request));
        verify(attendance, never()).save(any());
    }

    @Test
    void attendanceByClassWithNoLearnersDoesNotQueryAttendanceTable() {
        when(learners.findBySchoolIdAndCurrentClassRoomId(1L, 99L, org.springframework.data.domain.Pageable.unpaged()))
                .thenReturn(org.springframework.data.domain.Page.empty());

        service.attendanceByClass(1L, LocalDate.of(2025, 1, 10), 99L, 1, PageRequest.of(0, 10));

        verify(attendance, never()).findBySchoolIdAndDateAndPeriodAndLearnerIdIn(any(), any(), any(), any(), any());
    }

    @Test
    void listLearnersRejectsInvalidStatusValue() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> service.listLearners(1L, "NOT_A_STATUS", null, null, null, Pageable.unpaged()));

        assertTrue(ex.getMessage().contains("LearnerStatus"));
        verify(learners, never()).findBySchoolId(any(), any());
    }
}
