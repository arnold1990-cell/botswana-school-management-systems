package com.bosams.schoolsetup.mapper;

import com.bosams.schoolsetup.domain.model.*;
import com.bosams.schoolsetup.dto.SchoolSetupDtos;

public final class SchoolSetupMapper {
    private SchoolSetupMapper() {
    }

    public static SchoolSetupDtos.SchoolProfileResponse toDto(SchoolProfile model) {
        return new SchoolSetupDtos.SchoolProfileResponse(model.getId(), model.getName(), model.getAddress(), model.getPhone(), model.getEmail(),
                model.getSchoolType(), model.getLogoFileKey(), model.getLogoFilename(), model.getLogoContentType(), model.getLogoSize(),
                model.getCreatedAt(), model.getUpdatedAt());
    }

    public static SchoolSetupDtos.AcademicYearResponse toDto(AcademicYear model) {
        return new SchoolSetupDtos.AcademicYearResponse(model.getId(), model.getYear(), model.getStartDate(), model.getEndDate(), model.isActive());
    }

    public static SchoolSetupDtos.TermResponse toDto(Term model) {
        return new SchoolSetupDtos.TermResponse(model.getId(), model.getName(), model.getAcademicYear().getId(), model.getStartDate(), model.getEndDate());
    }

    public static SchoolSetupDtos.GradeResponse toDto(Grade model) {
        return new SchoolSetupDtos.GradeResponse(model.getId(), model.getName(), model.getSortOrder());
    }

    public static SchoolSetupDtos.SchoolClassResponse toDto(SchoolClass model) {
        return new SchoolSetupDtos.SchoolClassResponse(model.getId(), model.getClassCode(), model.getClassName(), model.getGrade().getId(), model.getAcademicYear().getId());
    }

    public static SchoolSetupDtos.CalendarEventResponse toDto(SchoolCalendarEvent model) {
        return new SchoolSetupDtos.CalendarEventResponse(model.getId(), model.getTitle(), model.getEventType(), model.getTerm().getId(),
                model.getStartDate(), model.getEndDate());
    }

    public static SchoolSetupDtos.MasterDataResponse toDto(AbstractMasterDataEntity model) {
        return new SchoolSetupDtos.MasterDataResponse(model.getId(), model.getName(), model.getCode(), model.isActive());
    }

    public static SchoolSetupDtos.MediaReferenceResponse toDto(LearnerPhoto model) {
        return new SchoolSetupDtos.MediaReferenceResponse(model.getId(), model.getLearnerId(), model.getFileKey(), model.getFilename(), model.getContentType(), model.getSize());
    }

    public static SchoolSetupDtos.MediaReferenceResponse toDto(StaffPhoto model) {
        return new SchoolSetupDtos.MediaReferenceResponse(model.getId(), model.getStaffId(), model.getFileKey(), model.getFilename(), model.getContentType(), model.getSize());
    }
}
