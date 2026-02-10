package com.bosams.hr;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bosams.common.ConflictException;
import com.bosams.hr.AttendanceCreateRequest;
import com.bosams.hr.LeaveDecisionRequest;
import com.bosams.hr.LeaveRequestCreate;
import com.bosams.hr.RegisterClassAssignRequest;
import com.bosams.hr.LeaveStatus;
import com.bosams.hr.LeaveType;
import com.bosams.hr.RegisterClassRole;
import com.bosams.hr.StaffAttendanceStatus;
import com.bosams.hr.StaffLeaveRequest;
import com.bosams.hr.StaffMember;
import com.bosams.hr.StaffType;
import com.bosams.hr.AppraisalRecordRepository;
import com.bosams.hr.EducatorSubjectExperienceRepository;
import com.bosams.hr.RegisterClassAssignmentRepository;
import com.bosams.hr.StaffAttendanceRecordRepository;
import com.bosams.hr.StaffLeaveRequestRepository;
import com.bosams.hr.StaffMemberRepository;
import com.bosams.hr.TrainingAttendanceRepository;
import com.bosams.hr.TrainingProgramRepository;
import com.bosams.hr.HrService;
import com.bosams.schoolsetup.repository.AcademicYearRepository;
import com.bosams.schoolsetup.repository.ClassRoomRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        service = new HrService(
                staffRepo,
                expRepo,
                registerRepo,
                leaveRepo,
                attendanceRepo,
                trainingRepo,
                trainingAttendanceRepo,
                appraisalRepo,
                academicYears,
                classRooms
        );
    }

    @Test
    void assignRegisterClassRejectsNonEducator() {
        var staff = new StaffMember();
        staff.setStaffType(StaffType.NON_TEACHING);
        staff.setSchoolId(1L);
        when(staffRepo.findById(10L)).thenReturn(Optional.of(staff));

        assertThrows(
                ConflictException.class,
                () -> service.assignRegisterClass(
                        new RegisterClassAssignRequest(1L, 10L, 2L, 3L, RegisterClassRole.REGISTER_TEACHER),
                        "qa"
                )
        );
    }

    @Test
    void createLeaveRejectsStaffFromAnotherSchool() {
        var staff = new StaffMember();
        staff.setSchoolId(99L);
        when(staffRepo.findById(10L)).thenReturn(Optional.of(staff));

        assertThrows(
                ConflictException.class,
                () -> service.createLeave(
                        new LeaveRequestCreate(1L, 10L, LeaveType.ANNUAL, LocalDate.now(), LocalDate.now(), null),
                        "qa"
                )
        );
    }

    @Test
    void createAttendanceRejectsDuplicate() {
        var staff = new StaffMember();
        staff.setSchoolId(1L);
        when(staffRepo.findById(10L)).thenReturn(Optional.of(staff));
        when(attendanceRepo.existsBySchoolIdAndStaffIdAndDate(1L, 10L, LocalDate.of(2025, 1, 2)))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> service.createAttendance(
                        new AttendanceCreateRequest(
                                1L,
                                LocalDate.of(2025, 1, 2),
                                10L,
                                StaffAttendanceStatus.PRESENT,
                                null,
                                null
                        ),
                        "qa"
                )
        );
        verify(attendanceRepo, never()).save(any());
    }

    @Test
    void rejectLeaveAfterApprovalFailsStateMachine() {
        var leave = new StaffLeaveRequest();
        leave.setStatus(LeaveStatus.APPROVED);
        when(leaveRepo.findById(5L)).thenReturn(Optional.of(leave));

        assertThrows(
                ConflictException.class,
                () -> service.rejectLeave(5L, new LeaveDecisionRequest("too late"), "qa")
        );
    }
}
