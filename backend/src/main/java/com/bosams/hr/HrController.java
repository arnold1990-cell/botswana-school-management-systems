package com.bosams.hr;

import com.bosams.hr.AppraisalCreateRequest;
import com.bosams.hr.AppraisalResponse;
import com.bosams.hr.AttendanceCreateRequest;
import com.bosams.hr.AttendanceResponse;
import com.bosams.hr.DashboardSummary;
import com.bosams.hr.LeaveDecisionRequest;
import com.bosams.hr.LeaveRequestCreate;
import com.bosams.hr.LeaveRequestResponse;
import com.bosams.hr.RegisterClassAssignRequest;
import com.bosams.hr.RegisterClassAssignmentResponse;
import com.bosams.hr.StaffArchiveRequest;
import com.bosams.hr.StaffResponse;
import com.bosams.hr.StaffUpsertRequest;
import com.bosams.hr.SubjectExperienceRequest;
import com.bosams.hr.SubjectExperienceResponse;
import com.bosams.hr.TrainingAttendanceBulkRequest;
import com.bosams.hr.TrainingAttendanceResponse;
import com.bosams.hr.TrainingProgramResponse;
import com.bosams.hr.TrainingProgramUpsertRequest;
import com.bosams.hr.AppraisalType;
import com.bosams.hr.LeaveStatus;
import com.bosams.hr.StaffStatus;
import com.bosams.hr.StaffType;
import com.bosams.hr.HrService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hr")
@Tag(name = "HR")
public class HrController {

    private final HrService service;

    public HrController(HrService service) {
        this.service = service;
    }

    @PostMapping("/staff")
    @ResponseStatus(HttpStatus.CREATED)
    StaffResponse createStaff(@RequestBody @Valid StaffUpsertRequest request) {
        return service.createStaff(request, "system");
    }

    @GetMapping("/staff/{id}")
    StaffResponse getStaff(@PathVariable Long id) {
        return service.getStaff(id);
    }

    @GetMapping("/staff")
    Page<StaffResponse> listStaff(
            @RequestParam Long schoolId,
            @RequestParam(required = false) StaffStatus status,
            @RequestParam(required = false) StaffType type,
            Pageable pageable
    ) {
        return service.listStaff(schoolId, status, type, pageable);
    }

    @PutMapping("/staff/{id}")
    StaffResponse updateStaff(@PathVariable Long id, @RequestBody @Valid StaffUpsertRequest request) {
        return service.updateStaff(id, request, "system");
    }

    @PostMapping("/staff/{id}/archive")
    StaffResponse archiveStaff(@PathVariable Long id, @RequestBody @Valid StaffArchiveRequest request) {
        return service.archive(id, request, "system");
    }

    @PostMapping("/staff/{id}/restore")
    StaffResponse restoreStaff(@PathVariable Long id) {
        return service.restore(id, "system");
    }

    @PostMapping("/educators/{staffId}/subject-experience")
    @ResponseStatus(HttpStatus.CREATED)
    SubjectExperienceResponse addSubjectExperience(
            @PathVariable Long staffId,
            @RequestBody @Valid SubjectExperienceRequest request
    ) {
        return service.addSubjectExperience(staffId, request, "system");
    }

    @GetMapping("/educators/{staffId}/subject-experience")
    Page<SubjectExperienceResponse> listSubjectExperience(
            @PathVariable Long staffId,
            @RequestParam Long schoolId,
            Pageable pageable
    ) {
        return service.listSubjectExperience(schoolId, staffId, pageable);
    }

    @DeleteMapping("/educators/{staffId}/subject-experience/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSubjectExperience(@PathVariable Long id) {
        service.deleteSubjectExperience(id);
    }

    @PostMapping("/register-classes/assign")
    @ResponseStatus(HttpStatus.CREATED)
    RegisterClassAssignmentResponse assignRegisterClass(
            @RequestBody @Valid RegisterClassAssignRequest request
    ) {
        return service.assignRegisterClass(request, "system");
    }

    @GetMapping("/register-classes")
    Page<RegisterClassAssignmentResponse> listRegisterClass(
            @RequestParam Long schoolId,
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) Long classRoomId,
            Pageable pageable
    ) {
        return service.listRegisterClass(schoolId, academicYearId, classRoomId, pageable);
    }

    @DeleteMapping("/register-classes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteRegisterClass(@PathVariable Long id) {
        service.deleteRegisterClass(id);
    }

    @PostMapping("/leave-requests")
    @ResponseStatus(HttpStatus.CREATED)
    LeaveRequestResponse createLeave(@RequestBody @Valid LeaveRequestCreate request) {
        return service.createLeave(request, "system");
    }

    @GetMapping("/leave-requests/{id}")
    LeaveRequestResponse getLeave(@PathVariable Long id) {
        return service.getLeave(id);
    }

    @GetMapping("/leave-requests")
    Page<LeaveRequestResponse> listLeave(
            @RequestParam Long schoolId,
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) Long staffId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            Pageable pageable
    ) {
        return service.listLeave(schoolId, status, staffId, from, to, pageable);
    }

    @PostMapping("/leave-requests/{id}/approve")
    LeaveRequestResponse approveLeave(
            @PathVariable Long id,
            @RequestBody(required = false) LeaveDecisionRequest request
    ) {
        return service.approveLeave(id, request == null ? new LeaveDecisionRequest(null) : request, "system");
    }

    @PostMapping("/leave-requests/{id}/reject")
    LeaveRequestResponse rejectLeave(
            @PathVariable Long id,
            @RequestBody(required = false) LeaveDecisionRequest request
    ) {
        return service.rejectLeave(id, request == null ? new LeaveDecisionRequest(null) : request, "system");
    }

    @PostMapping("/leave-requests/{id}/cancel")
    LeaveRequestResponse cancelLeave(
            @PathVariable Long id,
            @RequestBody(required = false) LeaveDecisionRequest request
    ) {
        return service.cancelLeave(id, request == null ? new LeaveDecisionRequest(null) : request, "system");
    }

    @PostMapping("/attendance")
    List<AttendanceResponse> createAttendance(@RequestBody @Valid AttendanceCreateRequest request) {
        return service.createAttendance(request, "system");
    }

    @GetMapping("/attendance")
    Page<AttendanceResponse> attendanceByDate(
            @RequestParam Long schoolId,
            @RequestParam LocalDate date,
            Pageable pageable
    ) {
        return service.attendanceByDate(schoolId, date, pageable);
    }

    @GetMapping("/attendance/staff/{staffId}")
    Page<AttendanceResponse> attendanceByStaff(
            @PathVariable Long staffId,
            @RequestParam Long schoolId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            Pageable pageable
    ) {
        return service.attendanceByStaff(schoolId, staffId, from, to, pageable);
    }

    @GetMapping("/dashboard/summary")
    DashboardSummary dashboard(
            @RequestParam Long schoolId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return service.dashboard(schoolId, from, to);
    }

    @PostMapping("/training-programs")
    @ResponseStatus(HttpStatus.CREATED)
    TrainingProgramResponse createTraining(@RequestBody @Valid TrainingProgramUpsertRequest request) {
        return service.createTraining(request, "system");
    }

    @GetMapping("/training-programs/{id}")
    TrainingProgramResponse getTraining(@PathVariable Long id) {
        return service.getTraining(id);
    }

    @GetMapping("/training-programs")
    Page<TrainingProgramResponse> listTraining(
            @RequestParam Long schoolId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            Pageable pageable
    ) {
        return service.listTraining(schoolId, from, to, pageable);
    }

    @PutMapping("/training-programs/{id}")
    TrainingProgramResponse updateTraining(
            @PathVariable Long id,
            @RequestBody @Valid TrainingProgramUpsertRequest request
    ) {
        return service.updateTraining(id, request, "system");
    }

    @DeleteMapping("/training-programs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTraining(@PathVariable Long id) {
        service.deleteTraining(id);
    }

    @PostMapping("/training-programs/{id}/attendance")
    List<TrainingAttendanceResponse> createTrainingAttendance(
            @PathVariable Long id,
            @RequestBody @Valid TrainingAttendanceBulkRequest request
    ) {
        return service.addTrainingAttendance(id, request, "system");
    }

    @GetMapping("/training-programs/{id}/attendance")
    Page<TrainingAttendanceResponse> listTrainingAttendance(
            @PathVariable Long id,
            @RequestParam Long schoolId,
            Pageable pageable
    ) {
        return service.listTrainingAttendance(schoolId, id, pageable);
    }

    @PostMapping("/appraisals")
    @ResponseStatus(HttpStatus.CREATED)
    AppraisalResponse createAppraisal(@RequestBody @Valid AppraisalCreateRequest request) {
        return service.createAppraisal(request, "system");
    }

    @GetMapping("/appraisals/{id}")
    AppraisalResponse getAppraisal(@PathVariable Long id) {
        return service.getAppraisal(id);
    }

    @GetMapping("/appraisals")
    Page<AppraisalResponse> listAppraisal(
            @RequestParam Long schoolId,
            @RequestParam(required = false) Long staffId,
            @RequestParam(required = false) AppraisalType type,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            Pageable pageable
    ) {
        return service.listAppraisal(schoolId, staffId, type, from, to, pageable);
    }
}
