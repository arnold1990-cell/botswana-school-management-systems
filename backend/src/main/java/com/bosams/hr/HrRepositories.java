package com.bosams.hr;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface StaffMemberRepository extends JpaRepository<StaffMember, Long> {
    boolean existsBySchoolIdAndStaffNumber(Long schoolId, String staffNumber);
    boolean existsBySchoolIdAndStaffNumberAndIdNot(Long schoolId, String staffNumber, Long id);
    Page<StaffMember> findBySchoolIdAndStatusAndStaffType(Long schoolId, StaffStatus status, StaffType staffType, Pageable p);
    Page<StaffMember> findBySchoolIdAndStatus(Long schoolId, StaffStatus status, Pageable p);
    Page<StaffMember> findBySchoolIdAndStaffType(Long schoolId, StaffType staffType, Pageable p);
    Page<StaffMember> findBySchoolId(Long schoolId, Pageable p);
}

interface EducatorSubjectExperienceRepository extends JpaRepository<EducatorSubjectExperience, Long> {
    Page<EducatorSubjectExperience> findBySchoolIdAndStaffId(Long schoolId, Long staffId, Pageable p);
}

interface RegisterClassAssignmentRepository extends JpaRepository<RegisterClassAssignment, Long> {
    Page<RegisterClassAssignment> findBySchoolIdAndAcademicYearIdAndClassRoomId(Long schoolId, Long yearId, Long classId, Pageable p);
    Page<RegisterClassAssignment> findBySchoolIdAndAcademicYearId(Long schoolId, Long yearId, Pageable p);
    Page<RegisterClassAssignment> findBySchoolId(Long schoolId, Pageable p);
}

interface StaffLeaveRequestRepository extends JpaRepository<StaffLeaveRequest, Long> {
    Page<StaffLeaveRequest> findBySchoolId(Long schoolId, Pageable p);
    Page<StaffLeaveRequest> findBySchoolIdAndStatus(Long schoolId, LeaveStatus status, Pageable p);
    Page<StaffLeaveRequest> findBySchoolIdAndStaffId(Long schoolId, Long staffId, Pageable p);
    Page<StaffLeaveRequest> findBySchoolIdAndStatusAndStaffId(Long schoolId, LeaveStatus status, Long staffId, Pageable p);
    Page<StaffLeaveRequest> findBySchoolIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(Long schoolId, LocalDate from, LocalDate to, Pageable p);
    long countBySchoolIdAndStatus(Long schoolId, LeaveStatus status);
    boolean existsBySchoolIdAndStaffIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long schoolId, Long staffId, LeaveStatus status, LocalDate date1, LocalDate date2);
}

interface StaffAttendanceRecordRepository extends JpaRepository<StaffAttendanceRecord, Long> {
    boolean existsBySchoolIdAndStaffIdAndDate(Long schoolId, Long staffId, LocalDate date);
    Page<StaffAttendanceRecord> findBySchoolIdAndDate(Long schoolId, LocalDate date, Pageable p);
    Page<StaffAttendanceRecord> findBySchoolIdAndStaffIdAndDateBetween(Long schoolId, Long staffId, LocalDate from, LocalDate to, Pageable p);
    Page<StaffAttendanceRecord> findBySchoolIdAndDateBetween(Long schoolId, LocalDate from, LocalDate to, Pageable p);
    long countBySchoolIdAndDateBetween(Long schoolId, LocalDate from, LocalDate to);
    long countBySchoolIdAndDateBetweenAndStatus(Long schoolId, LocalDate from, LocalDate to, StaffAttendanceStatus status);
}

interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Long> {
    Page<TrainingProgram> findBySchoolIdAndStartDateBetween(Long schoolId, LocalDate from, LocalDate to, Pageable p);
    Page<TrainingProgram> findBySchoolId(Long schoolId, Pageable p);
}

interface TrainingAttendanceRepository extends JpaRepository<TrainingAttendance, Long> {
    Page<TrainingAttendance> findBySchoolIdAndTrainingProgramId(Long schoolId, Long programId, Pageable p);
}

interface AppraisalRecordRepository extends JpaRepository<AppraisalRecord, Long> {
    Page<AppraisalRecord> findBySchoolId(Long schoolId, Pageable p);
    Page<AppraisalRecord> findBySchoolIdAndStaffId(Long schoolId, Long staffId, Pageable p);
    Page<AppraisalRecord> findBySchoolIdAndAppraisalType(Long schoolId, AppraisalType type, Pageable p);
    Page<AppraisalRecord> findBySchoolIdAndStaffIdAndAppraisalType(Long schoolId, Long staffId, AppraisalType type, Pageable p);
    Page<AppraisalRecord> findBySchoolIdAndAppraisalDateBetween(Long schoolId, LocalDate from, LocalDate to, Pageable p);
}

