package com.bosams.hr;

import com.bosams.hr.HrDtos.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hr")
@Tag(name = "HR")
class HrController {
    private final HrService service;
    HrController(HrService service) { this.service = service; }

    @PostMapping("/staff") @ResponseStatus(HttpStatus.CREATED) StaffResponse createStaff(@RequestBody @Valid StaffUpsertRequest r){ return service.createStaff(r, "system"); }
    @GetMapping("/staff/{id}") StaffResponse getStaff(@PathVariable Long id){ return service.getStaff(id); }
    @GetMapping("/staff") Page<StaffResponse> listStaff(@RequestParam Long schoolId, @RequestParam(required = false) StaffStatus status, @RequestParam(required = false) StaffType type, Pageable p){ return service.listStaff(schoolId, status, type, p); }
    @PutMapping("/staff/{id}") StaffResponse updateStaff(@PathVariable Long id, @RequestBody @Valid StaffUpsertRequest r){ return service.updateStaff(id, r, "system"); }
    @PostMapping("/staff/{id}/archive") StaffResponse archiveStaff(@PathVariable Long id, @RequestBody @Valid StaffArchiveRequest r){ return service.archive(id, r, "system"); }
    @PostMapping("/staff/{id}/restore") StaffResponse restoreStaff(@PathVariable Long id){ return service.restore(id, "system"); }

    @PostMapping("/educators/{staffId}/subject-experience") @ResponseStatus(HttpStatus.CREATED) SubjectExperienceResponse addSubjectExperience(@PathVariable Long staffId, @RequestBody @Valid SubjectExperienceRequest r){ return service.addSubjectExperience(staffId, r, "system"); }
    @GetMapping("/educators/{staffId}/subject-experience") Page<SubjectExperienceResponse> listSubjectExperience(@PathVariable Long staffId, @RequestParam Long schoolId, Pageable p){ return service.listSubjectExperience(schoolId, staffId, p); }
    @DeleteMapping("/educators/{staffId}/subject-experience/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void deleteSubjectExperience(@PathVariable Long staffId, @PathVariable Long id){ service.deleteSubjectExperience(id); }

    @PostMapping("/register-classes/assign") @ResponseStatus(HttpStatus.CREATED) RegisterClassAssignmentResponse assignRegisterClass(@RequestBody @Valid RegisterClassAssignRequest r){ return service.assignRegisterClass(r, "system"); }
    @GetMapping("/register-classes") Page<RegisterClassAssignmentResponse> listRegisterClass(@RequestParam Long schoolId, @RequestParam(required = false) Long academicYearId, @RequestParam(required = false) Long classRoomId, Pageable p){ return service.listRegisterClass(schoolId, academicYearId, classRoomId, p); }
    @DeleteMapping("/register-classes/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void deleteRegisterClass(@PathVariable Long id){ service.deleteRegisterClass(id); }

    @PostMapping("/leave-requests") @ResponseStatus(HttpStatus.CREATED) LeaveRequestResponse createLeave(@RequestBody @Valid LeaveRequestCreate r){ return service.createLeave(r, "system"); }
    @GetMapping("/leave-requests/{id}") LeaveRequestResponse getLeave(@PathVariable Long id){ return service.getLeave(id); }
    @GetMapping("/leave-requests") Page<LeaveRequestResponse> listLeave(@RequestParam Long schoolId, @RequestParam(required = false) LeaveStatus status, @RequestParam(required = false) Long staffId, @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to, Pageable p){ return service.listLeave(schoolId, status, staffId, from, to, p); }
    @PostMapping("/leave-requests/{id}/approve") LeaveRequestResponse approveLeave(@PathVariable Long id, @RequestBody(required = false) LeaveDecisionRequest r){ return service.approveLeave(id, r == null ? new LeaveDecisionRequest(null) : r, "system"); }
    @PostMapping("/leave-requests/{id}/reject") LeaveRequestResponse rejectLeave(@PathVariable Long id, @RequestBody(required = false) LeaveDecisionRequest r){ return service.rejectLeave(id, r == null ? new LeaveDecisionRequest(null) : r, "system"); }
    @PostMapping("/leave-requests/{id}/cancel") LeaveRequestResponse cancelLeave(@PathVariable Long id, @RequestBody(required = false) LeaveDecisionRequest r){ return service.cancelLeave(id, r == null ? new LeaveDecisionRequest(null) : r, "system"); }

    @PostMapping("/attendance") List<AttendanceResponse> createAttendance(@RequestBody @Valid AttendanceCreateRequest r){ return service.createAttendance(r, "system"); }
    @GetMapping("/attendance") Page<AttendanceResponse> attendanceByDate(@RequestParam Long schoolId, @RequestParam LocalDate date, Pageable p){ return service.attendanceByDate(schoolId, date, p); }
    @GetMapping("/attendance/staff/{staffId}") Page<AttendanceResponse> attendanceByStaff(@PathVariable Long staffId, @RequestParam Long schoolId, @RequestParam LocalDate from, @RequestParam LocalDate to, Pageable p){ return service.attendanceByStaff(schoolId, staffId, from, to, p); }
    @GetMapping("/dashboard/summary") DashboardSummary dashboard(@RequestParam Long schoolId, @RequestParam LocalDate from, @RequestParam LocalDate to){ return service.dashboard(schoolId, from, to); }

    @PostMapping("/training-programs") @ResponseStatus(HttpStatus.CREATED) TrainingProgramResponse createTraining(@RequestBody @Valid TrainingProgramUpsertRequest r){ return service.createTraining(r, "system"); }
    @GetMapping("/training-programs/{id}") TrainingProgramResponse getTraining(@PathVariable Long id){ return service.getTraining(id); }
    @GetMapping("/training-programs") Page<TrainingProgramResponse> listTraining(@RequestParam Long schoolId, @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to, Pageable p){ return service.listTraining(schoolId, from, to, p); }
    @PutMapping("/training-programs/{id}") TrainingProgramResponse updateTraining(@PathVariable Long id, @RequestBody @Valid TrainingProgramUpsertRequest r){ return service.updateTraining(id, r, "system"); }
    @DeleteMapping("/training-programs/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void deleteTraining(@PathVariable Long id){ service.deleteTraining(id); }
    @PostMapping("/training-programs/{id}/attendance") List<TrainingAttendanceResponse> createTrainingAttendance(@PathVariable Long id, @RequestBody @Valid TrainingAttendanceBulkRequest r){ return service.addTrainingAttendance(id, r, "system"); }
    @GetMapping("/training-programs/{id}/attendance") Page<TrainingAttendanceResponse> listTrainingAttendance(@PathVariable Long id, @RequestParam Long schoolId, Pageable p){ return service.listTrainingAttendance(schoolId, id, p); }

    @PostMapping("/appraisals") @ResponseStatus(HttpStatus.CREATED) AppraisalResponse createAppraisal(@RequestBody @Valid AppraisalCreateRequest r){ return service.createAppraisal(r, "system"); }
    @GetMapping("/appraisals/{id}") AppraisalResponse getAppraisal(@PathVariable Long id){ return service.getAppraisal(id); }
    @GetMapping("/appraisals") Page<AppraisalResponse> listAppraisal(@RequestParam Long schoolId, @RequestParam(required = false) Long staffId, @RequestParam(required = false) AppraisalType type, @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to, Pageable p){ return service.listAppraisal(schoolId, staffId, type, from, to, p); }
}
