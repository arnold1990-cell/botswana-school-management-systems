package com.bosams.schoolsetup.api;

import com.bosams.schoolsetup.dto.SchoolSetupDtos;
import com.bosams.schoolsetup.service.SchoolSetupService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/school-setup")
public class SchoolSetupController {

    private final SchoolSetupService service;

    // Foundational entry point: every module reads this profile for identity and contact metadata.
    @PostMapping("/school-profile")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.SchoolProfileResponse createSchoolProfile(@RequestBody @Valid SchoolSetupDtos.SchoolProfileRequest request) {
        return service.upsertSchoolProfile(null, request);
    }

    @PutMapping("/school-profile/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.SchoolProfileResponse updateSchoolProfile(@PathVariable UUID id,
                                                                     @RequestBody @Valid SchoolSetupDtos.SchoolProfileRequest request) {
        return service.upsertSchoolProfile(id, request);
    }

    @PostMapping("/academic-years")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.AcademicYearResponse createAcademicYear(@RequestBody @Valid SchoolSetupDtos.AcademicYearRequest request) {
        return service.saveAcademicYear(null, request);
    }

    @PutMapping("/academic-years/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.AcademicYearResponse updateAcademicYear(@PathVariable UUID id,
                                                                   @RequestBody @Valid SchoolSetupDtos.AcademicYearRequest request) {
        return service.saveAcademicYear(id, request);
    }

    @GetMapping("/academic-years")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public Page<SchoolSetupDtos.AcademicYearResponse> listAcademicYears(Pageable pageable) { return service.listAcademicYears(pageable); }

    @DeleteMapping("/academic-years/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public void deleteAcademicYear(@PathVariable UUID id) { service.deleteAcademicYear(id); }

    @PostMapping("/terms")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.TermResponse createTerm(@RequestBody @Valid SchoolSetupDtos.TermRequest request) { return service.saveTerm(null, request); }

    @PutMapping("/terms/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.TermResponse updateTerm(@PathVariable UUID id, @RequestBody @Valid SchoolSetupDtos.TermRequest request) { return service.saveTerm(id, request); }

    @GetMapping("/academic-years/{academicYearId}/terms")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public Page<SchoolSetupDtos.TermResponse> listTerms(@PathVariable UUID academicYearId, Pageable pageable) { return service.listTerms(academicYearId, pageable); }

    @DeleteMapping("/terms/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public void deleteTerm(@PathVariable UUID id) { service.deleteTerm(id); }

    @PostMapping("/grades")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.GradeResponse createGrade(@RequestBody @Valid SchoolSetupDtos.GradeRequest request) { return service.saveGrade(null, request); }

    @PutMapping("/grades/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.GradeResponse updateGrade(@PathVariable UUID id, @RequestBody @Valid SchoolSetupDtos.GradeRequest request) { return service.saveGrade(id, request); }

    @GetMapping("/grades")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public Page<SchoolSetupDtos.GradeResponse> listGrades(Pageable pageable) { return service.listGrades(pageable); }

    @DeleteMapping("/grades/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public void deleteGrade(@PathVariable UUID id) { service.deleteGrade(id); }

    @PostMapping("/classes")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.SchoolClassResponse createClass(@RequestBody @Valid SchoolSetupDtos.SchoolClassRequest request) { return service.saveClass(null, request); }

    @PutMapping("/classes/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.SchoolClassResponse updateClass(@PathVariable UUID id, @RequestBody @Valid SchoolSetupDtos.SchoolClassRequest request) { return service.saveClass(id, request); }

    @GetMapping("/academic-years/{academicYearId}/classes")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public Page<SchoolSetupDtos.SchoolClassResponse> listClasses(@PathVariable UUID academicYearId, Pageable pageable) { return service.listClasses(academicYearId, pageable); }

    @PostMapping("/calendar-events")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.CalendarEventResponse createCalendarEvent(@RequestBody @Valid SchoolSetupDtos.CalendarEventRequest request) {
        return service.saveCalendarEvent(null, request);
    }

    @PutMapping("/calendar-events/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.CalendarEventResponse updateCalendarEvent(@PathVariable UUID id,
                                                                     @RequestBody @Valid SchoolSetupDtos.CalendarEventRequest request) {
        return service.saveCalendarEvent(id, request);
    }

    @GetMapping("/calendar-events")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public Page<SchoolSetupDtos.CalendarEventResponse> listCalendarEvents(Pageable pageable) { return service.listCalendarEvents(pageable); }

    @PostMapping("/master-data/{type}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.MasterDataResponse createMasterData(@PathVariable String type,
                                                               @RequestBody @Valid SchoolSetupDtos.MasterDataRequest request) {
        return service.saveMasterData(type, null, request);
    }

    @PutMapping("/master-data/{type}/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.MasterDataResponse updateMasterData(@PathVariable String type, @PathVariable UUID id,
                                                               @RequestBody @Valid SchoolSetupDtos.MasterDataRequest request) {
        return service.saveMasterData(type, id, request);
    }

    @GetMapping("/master-data/{type}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public Page<SchoolSetupDtos.MasterDataResponse> listMasterData(@PathVariable String type, Pageable pageable) {
        return service.listMasterData(type, pageable);
    }

    @DeleteMapping("/master-data/{type}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public void deactivateMasterData(@PathVariable String type, @PathVariable UUID id) {
        service.deactivateMasterData(type, id);
    }

    @PostMapping("/learner-photos")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.MediaReferenceResponse createLearnerPhoto(@RequestBody @Valid SchoolSetupDtos.MediaReferenceRequest request) {
        return service.saveLearnerPhoto(null, request);
    }

    @PutMapping("/learner-photos/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.MediaReferenceResponse updateLearnerPhoto(@PathVariable UUID id,
                                                                     @RequestBody @Valid SchoolSetupDtos.MediaReferenceRequest request) {
        return service.saveLearnerPhoto(id, request);
    }

    @GetMapping("/learners/{learnerId}/photos")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public Page<SchoolSetupDtos.MediaReferenceResponse> listLearnerPhotos(@PathVariable UUID learnerId, Pageable pageable) {
        return service.listLearnerPhotos(learnerId, pageable);
    }

    @PostMapping("/staff-photos")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.MediaReferenceResponse createStaffPhoto(@RequestBody @Valid SchoolSetupDtos.MediaReferenceRequest request) {
        return service.saveStaffPhoto(null, request);
    }

    @PutMapping("/staff-photos/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public SchoolSetupDtos.MediaReferenceResponse updateStaffPhoto(@PathVariable UUID id,
                                                                   @RequestBody @Valid SchoolSetupDtos.MediaReferenceRequest request) {
        return service.saveStaffPhoto(id, request);
    }

    @GetMapping("/staff/{staffId}/photos")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN','SUPER_ADMIN')")
    public Page<SchoolSetupDtos.MediaReferenceResponse> listStaffPhotos(@PathVariable UUID staffId, Pageable pageable) {
        return service.listStaffPhotos(staffId, pageable);
    }
}
