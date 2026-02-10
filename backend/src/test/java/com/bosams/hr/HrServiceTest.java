package com.bosams.hr;

import com.bosams.common.ConflictException;
import com.bosams.hr.HrDtos.LeaveDecisionRequest;
import com.bosams.hr.HrDtos.AttendanceCreateRequest;
import com.bosams.hr.HrDtos.LeaveRequestCreate;
import com.bosams.hr.HrDtos.RegisterClassAssignRequest;
import com.bosams.schoolsetup.repository.AcademicYearRepository;
import com.bosams.schoolsetup.repository.ClassRoomRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HrServiceTest {

    @Mock StaffMemberRepository staffRepo;
    @Mock EducatorSubjectExperienceRepository expRepo;
    @Mock RegisterClassAssignmentRepository registerRepo;
    @Mock StaffLeaveRequestRepository leaveRepo;
    @Mock StaffAttendanceRecordRepository attendanceRepo;
    @Mock TrainingProgramRepository trainingRepo;
    @Mock TrainingAttendanceRepository trainingAttendanceRepo;
    @Mock AppraisalRecordRepository appraisalRepo;
    @Mock AcademicYearRepository academicYears;
    @Mock ClassRoomRepository classRooms;

    HrService service;

    @BeforeEach
    void setUp() {
        service = new HrService(staffRepo, expRepo, registerRepo, leaveRepo, attendanceRepo, trainingRepo,
                trainingAttendanceRepo, appraisalRepo, academicYears, classRooms);
    }

    @Test
    void assignRegisterClassRejectsNonEducator() {
        var staff = new StaffMember();
        staff.setStaffType(StaffType.NON_TEACHING);
        staff.setSchoolId(1L);
        when(staffRepo.findById(10L)).thenReturn(Optional.of(staff));

        assertThrows(ConflictException.class,
                () -> service.assignRegisterClass(new RegisterClassAssignRequest(1L, 10L, 2L, 3L, RegisterClassRole.REGISTER_TEACHER), "qa"));
    }

    @Test
    void createLeaveRejectsStaffFromAnotherSchool() {
        var staff = new StaffMember();
        staff.setSchoolId(99L);
        when(staffRepo.findById(10L)).thenReturn(Optional.of(staff));

        assertThrows(ConflictException.class,
                () -> service.createLeave(new LeaveRequestCreate(1L, 10L, LeaveType.ANNUAL, LocalDate.now(), LocalDate.now(), null), "qa"));
    }

    @Test
    void createAttendanceRejectsDuplicate() {
        var staff = new StaffMember();
        staff.setSchoolId(1L);
        when(staffRepo.findById(10L)).thenReturn(Optional.of(staff));
        when(attendanceRepo.existsBySchoolIdAndStaffIdAndDate(1L, 10L, LocalDate.of(2025, 1, 2))).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.createAttendance(new AttendanceCreateRequest(1L, LocalDate.of(2025, 1, 2), 10L, StaffAttendanceStatus.PRESENT, null, null), "qa"));
        verify(attendanceRepo, never()).save(any());
    }

    @Test
    void rejectLeaveAfterApprovalFailsStateMachine() {
        var leave = new StaffLeaveRequest();
        leave.setStatus(LeaveStatus.APPROVED);
        when(leaveRepo.findById(5L)).thenReturn(Optional.of(leave));

        assertThrows(ConflictException.class,
                () -> service.rejectLeave(5L, new LeaveDecisionRequest("too late"), "qa"));
    }
}
