package com.bosams.schoolsetup.service;

import com.bosams.common.ConflictException;
import com.bosams.common.NotFoundException;
import com.bosams.schoolsetup.domain.model.*;
import com.bosams.schoolsetup.dto.SchoolSetupDtos;
import com.bosams.schoolsetup.mapper.SchoolSetupMapper;
import com.bosams.schoolsetup.repository.*;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SchoolSetupService {
    private final SchoolProfileRepository schoolProfileRepository;
    private final AcademicYearV2Repository academicYearRepository;
    private final TermV2Repository termRepository;
    private final GradeV2Repository gradeRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SchoolCalendarEventRepository calendarEventRepository;
    private final LearnerPhotoRepository learnerPhotoRepository;
    private final StaffPhotoRepository staffPhotoRepository;

    private final BusRouteRepository busRouteRepository;
    private final BusTicketTypeRepository busTicketTypeRepository;
    private final MeritCodeRepository meritCodeRepository;
    private final DemeritCodeRepository demeritCodeRepository;
    private final HostelRepository hostelRepository;
    private final HouseRepository houseRepository;
    private final ExtraMuralRepository extraMuralRepository;
    private final TeamRepository teamRepository;
    private final CompetitionRepository competitionRepository;
    private final CompetitionEventRepository competitionEventRepository;
    private final SubjectRepository subjectRepository;

    public SchoolSetupDtos.SchoolProfileResponse upsertSchoolProfile(UUID id, SchoolSetupDtos.SchoolProfileRequest request) {
        SchoolProfile profile = id == null ? new SchoolProfile() : schoolProfileRepository.findById(id).orElseThrow(() -> new NotFoundException("School profile not found"));
        profile.setName(request.name());
        profile.setAddress(request.address());
        profile.setPhone(request.phone());
        profile.setEmail(request.email());
        profile.setSchoolType(request.schoolType());
        profile.setLogoFileKey(request.logoFileKey());
        profile.setLogoFilename(request.logoFilename());
        profile.setLogoContentType(request.logoContentType());
        profile.setLogoSize(request.logoSize());
        return SchoolSetupMapper.toDto(schoolProfileRepository.save(profile));
    }

    @Transactional
    public void deleteAcademicYear(UUID id) {
        if (termRepository.countByAcademicYearId(id) > 0 || schoolClassRepository.countByAcademicYearId(id) > 0) {
            throw new ConflictException("Cannot delete academic year that is already in use");
        }
        academicYearRepository.deleteById(id);
    }

    public SchoolSetupDtos.AcademicYearResponse saveAcademicYear(UUID id, SchoolSetupDtos.AcademicYearRequest request) {
        AcademicYear model = id == null ? new AcademicYear() : academicYearRepository.findById(id).orElseThrow(() -> new NotFoundException("Academic year not found"));
        model.setYear(request.year());
        model.setStartDate(request.startDate());
        model.setEndDate(request.endDate());
        model.setActive(request.active());
        return SchoolSetupMapper.toDto(academicYearRepository.save(model));
    }

    public SchoolSetupDtos.TermResponse saveTerm(UUID id, SchoolSetupDtos.TermRequest request) {
        var year = academicYearRepository.findById(request.academicYearId()).orElseThrow(() -> new NotFoundException("Academic year not found"));
        Term model = id == null ? new Term() : termRepository.findById(id).orElseThrow(() -> new NotFoundException("Term not found"));
        model.setName(request.name());
        model.setAcademicYear(year);
        model.setStartDate(request.startDate());
        model.setEndDate(request.endDate());
        return SchoolSetupMapper.toDto(termRepository.save(model));
    }

    public void deleteTerm(UUID id) {
        if (calendarEventRepository.countByTermId(id) > 0) {
            throw new ConflictException("Cannot delete term that is already referenced by calendar events");
        }
        termRepository.deleteById(id);
    }

    public SchoolSetupDtos.GradeResponse saveGrade(UUID id, SchoolSetupDtos.GradeRequest request) {
        Grade model = id == null ? new Grade() : gradeRepository.findById(id).orElseThrow(() -> new NotFoundException("Grade not found"));
        model.setName(request.name());
        model.setSortOrder(request.sortOrder());
        return SchoolSetupMapper.toDto(gradeRepository.save(model));
    }

    public void deleteGrade(UUID id) {
        if (schoolClassRepository.countByGradeId(id) > 0) {
            throw new ConflictException("Cannot delete grade already in use by classes");
        }
        gradeRepository.deleteById(id);
    }

    public SchoolSetupDtos.SchoolClassResponse saveClass(UUID id, SchoolSetupDtos.SchoolClassRequest request) {
        var grade = gradeRepository.findById(request.gradeId()).orElseThrow(() -> new NotFoundException("Grade not found"));
        var year = academicYearRepository.findById(request.academicYearId()).orElseThrow(() -> new NotFoundException("Academic year not found"));
        SchoolClass model = id == null ? new SchoolClass() : schoolClassRepository.findById(id).orElseThrow(() -> new NotFoundException("Class not found"));
        model.setClassCode(request.classCode());
        model.setClassName(request.className());
        model.setGrade(grade);
        model.setAcademicYear(year);
        return SchoolSetupMapper.toDto(schoolClassRepository.save(model));
    }

    public SchoolSetupDtos.CalendarEventResponse saveCalendarEvent(UUID id, SchoolSetupDtos.CalendarEventRequest request) {
        var term = termRepository.findById(request.termId()).orElseThrow(() -> new NotFoundException("Term not found"));
        SchoolCalendarEvent model = id == null ? new SchoolCalendarEvent() : calendarEventRepository.findById(id).orElseThrow(() -> new NotFoundException("Calendar event not found"));
        model.setTitle(request.title());
        model.setEventType(request.eventType());
        model.setTerm(term);
        model.setStartDate(request.startDate());
        model.setEndDate(request.endDate());
        return SchoolSetupMapper.toDto(calendarEventRepository.save(model));
    }

    public Page<SchoolSetupDtos.MediaReferenceResponse> listLearnerPhotos(UUID learnerId, Pageable pageable) {
        return learnerPhotoRepository.findByLearnerId(learnerId, pageable).map(SchoolSetupMapper::toDto);
    }

    public SchoolSetupDtos.MediaReferenceResponse saveLearnerPhoto(UUID id, SchoolSetupDtos.MediaReferenceRequest request) {
        LearnerPhoto model = id == null ? new LearnerPhoto() : learnerPhotoRepository.findById(id).orElseThrow(() -> new NotFoundException("Learner photo not found"));
        model.setLearnerId(request.ownerId());
        model.setFileKey(request.fileKey());
        model.setFilename(request.filename());
        model.setContentType(request.contentType());
        model.setSize(request.size());
        return SchoolSetupMapper.toDto(learnerPhotoRepository.save(model));
    }

    public Page<SchoolSetupDtos.MediaReferenceResponse> listStaffPhotos(UUID staffId, Pageable pageable) {
        return staffPhotoRepository.findByStaffId(staffId, pageable).map(SchoolSetupMapper::toDto);
    }

    public SchoolSetupDtos.MediaReferenceResponse saveStaffPhoto(UUID id, SchoolSetupDtos.MediaReferenceRequest request) {
        StaffPhoto model = id == null ? new StaffPhoto() : staffPhotoRepository.findById(id).orElseThrow(() -> new NotFoundException("Staff photo not found"));
        model.setStaffId(request.ownerId());
        model.setFileKey(request.fileKey());
        model.setFilename(request.filename());
        model.setContentType(request.contentType());
        model.setSize(request.size());
        return SchoolSetupMapper.toDto(staffPhotoRepository.save(model));
    }

    public Page<SchoolSetupDtos.MasterDataResponse> listMasterData(String type, Pageable pageable) {
        return resolveRepository(type).findAll(pageable).map(entity -> SchoolSetupMapper.toDto((AbstractMasterDataEntity) entity));
    }

    public SchoolSetupDtos.MasterDataResponse saveMasterData(String type, UUID id, SchoolSetupDtos.MasterDataRequest request) {
        AbstractMasterDataEntity model = id == null ? createByType(type) : (AbstractMasterDataEntity) resolveRepository(type)
                .findById(id).orElseThrow(() -> new NotFoundException(type + " item not found"));
        model.setName(request.name());
        model.setCode(request.code());
        model.setActive(request.active());
        return SchoolSetupMapper.toDto((AbstractMasterDataEntity) resolveRepository(type).save(model));
    }

    public void deactivateMasterData(String type, UUID id) {
        AbstractMasterDataEntity model = (AbstractMasterDataEntity) resolveRepository(type).findById(id)
                .orElseThrow(() -> new NotFoundException(type + " item not found"));
        model.setActive(false);
        resolveRepository(type).save(model);
    }

    public Page<SchoolSetupDtos.AcademicYearResponse> listAcademicYears(Pageable pageable) { return academicYearRepository.findAll(pageable).map(SchoolSetupMapper::toDto); }
    public Page<SchoolSetupDtos.TermResponse> listTerms(UUID yearId, Pageable pageable) { return termRepository.findByAcademicYearId(yearId, pageable).map(SchoolSetupMapper::toDto); }
    public Page<SchoolSetupDtos.GradeResponse> listGrades(Pageable pageable) { return gradeRepository.findAll(pageable).map(SchoolSetupMapper::toDto); }
    public Page<SchoolSetupDtos.SchoolClassResponse> listClasses(UUID yearId, Pageable pageable) { return schoolClassRepository.findByAcademicYearId(yearId, pageable).map(SchoolSetupMapper::toDto); }
    public Page<SchoolSetupDtos.CalendarEventResponse> listCalendarEvents(Pageable pageable) { return calendarEventRepository.findAll(pageable).map(SchoolSetupMapper::toDto); }

    private JpaRepository<?, UUID> resolveRepository(String type) {
        return switch (type.toLowerCase()) {
            case "bus-routes" -> busRouteRepository;
            case "bus-ticket-types" -> busTicketTypeRepository;
            case "merit-codes" -> meritCodeRepository;
            case "demerit-codes" -> demeritCodeRepository;
            case "hostels" -> hostelRepository;
            case "houses" -> houseRepository;
            case "extra-murals" -> extraMuralRepository;
            case "teams" -> teamRepository;
            case "competitions" -> competitionRepository;
            case "competition-events" -> competitionEventRepository;
            case "subjects" -> subjectRepository;
            default -> throw new NotFoundException("Unsupported master data type: " + type);
        };
    }

    private AbstractMasterDataEntity createByType(String type) {
        Map<String, Supplier<AbstractMasterDataEntity>> factories = Map.of(
                "bus-routes", BusRoute::new,
                "bus-ticket-types", BusTicketType::new,
                "merit-codes", MeritCode::new,
                "demerit-codes", DemeritCode::new,
                "hostels", Hostel::new,
                "houses", House::new,
                "extra-murals", ExtraMural::new,
                "teams", Team::new,
                "competitions", Competition::new,
                "competition-events", CompetitionEvent::new,
                "subjects", Subject::new
        );
        var supplier = factories.get(type.toLowerCase());
        if (supplier == null) {
            throw new NotFoundException("Unsupported master data type: " + type);
        }
        return supplier.get();
    }
}
