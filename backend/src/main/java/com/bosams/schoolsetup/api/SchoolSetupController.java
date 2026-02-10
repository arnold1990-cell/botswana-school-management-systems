package com.bosams.schoolsetup.api;

import com.bosams.common.NotFoundException;
import com.bosams.schoolsetup.domain.AcademicYear;
import com.bosams.schoolsetup.domain.BusRoute;
import com.bosams.schoolsetup.domain.BusTicketType;
import com.bosams.schoolsetup.domain.ClassRoom;
import com.bosams.schoolsetup.domain.Competition;
import com.bosams.schoolsetup.domain.DemeritCode;
import com.bosams.schoolsetup.domain.ExtraMuralActivity;
import com.bosams.schoolsetup.domain.ExtraMuralType;
import com.bosams.schoolsetup.domain.Grade;
import com.bosams.schoolsetup.domain.House;
import com.bosams.schoolsetup.domain.Hostel;
import com.bosams.schoolsetup.domain.MeritCode;
import com.bosams.schoolsetup.domain.School;
import com.bosams.schoolsetup.domain.Sport;
import com.bosams.schoolsetup.domain.Team;
import com.bosams.schoolsetup.domain.Term;
import com.bosams.schoolsetup.dto.Dtos.AcademicYearRequest;
import com.bosams.schoolsetup.dto.Dtos.AcademicYearResponse;
import com.bosams.schoolsetup.dto.Dtos.ClassRoomRequest;
import com.bosams.schoolsetup.dto.Dtos.ClassRoomResponse;
import com.bosams.schoolsetup.dto.Dtos.CodeRequest;
import com.bosams.schoolsetup.dto.Dtos.CodeResponse;
import com.bosams.schoolsetup.dto.Dtos.CompetitionRequest;
import com.bosams.schoolsetup.dto.Dtos.ExtraMuralActivityRequest;
import com.bosams.schoolsetup.dto.Dtos.GradeRequest;
import com.bosams.schoolsetup.dto.Dtos.GradeResponse;
import com.bosams.schoolsetup.dto.Dtos.NamedRequest;
import com.bosams.schoolsetup.dto.Dtos.NamedResponse;
import com.bosams.schoolsetup.dto.Dtos.SchoolRequest;
import com.bosams.schoolsetup.dto.Dtos.SchoolResponse;
import com.bosams.schoolsetup.dto.Dtos.SetActiveAcademicYearRequest;
import com.bosams.schoolsetup.dto.Dtos.TeamRequest;
import com.bosams.schoolsetup.dto.Dtos.TermRequest;
import com.bosams.schoolsetup.dto.Dtos.TermResponse;
import com.bosams.schoolsetup.repository.*;
import com.bosams.schoolsetup.service.SchoolSetupValidationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/school-setup")
public class SchoolSetupController {
    private final SchoolRepository schools;
    private final AcademicYearRepository years;
    private final TermRepository terms;
    private final GradeRepository grades;
    private final ClassRoomRepository classes;
    private final HouseRepository houses;
    private final ExtraMuralTypeRepository types;
    private final ExtraMuralActivityRepository acts;
    private final SportRepository sports;
    private final TeamRepository teams;
    private final CompetitionRepository comps;
    private final BusRouteRepository routes;
    private final BusTicketTypeRepository tickets;
    private final MeritCodeRepository merits;
    private final DemeritCodeRepository demerits;
    private final HostelRepository hostels;
    private final SchoolSetupValidationService uniqueness;

    public SchoolSetupController(
            SchoolRepository schools,
            AcademicYearRepository years,
            TermRepository terms,
            GradeRepository grades,
            ClassRoomRepository classes,
            HouseRepository houses,
            ExtraMuralTypeRepository types,
            ExtraMuralActivityRepository acts,
            SportRepository sports,
            TeamRepository teams,
            CompetitionRepository comps,
            BusRouteRepository routes,
            BusTicketTypeRepository tickets,
            MeritCodeRepository merits,
            DemeritCodeRepository demerits,
            HostelRepository hostels,
            SchoolSetupValidationService uniqueness) {
        this.schools = schools;
        this.years = years;
        this.terms = terms;
        this.grades = grades;
        this.classes = classes;
        this.houses = houses;
        this.types = types;
        this.acts = acts;
        this.sports = sports;
        this.teams = teams;
        this.comps = comps;
        this.routes = routes;
        this.tickets = tickets;
        this.merits = merits;
        this.demerits = demerits;
        this.hostels = hostels;
        this.uniqueness = uniqueness;
    }

    @PostMapping("/schools")
    public SchoolResponse createSchool(@RequestBody @Valid SchoolRequest r) {
        var s = new School();
        apply(s, r);
        return map(schools.save(s));
    }

    @GetMapping("/schools/{id}")
    public SchoolResponse getSchool(@PathVariable Long id) {
        return map(schools.findById(id).orElseThrow(() -> new NotFoundException("School not found")));
    }

    @PutMapping("/schools/{id}")
    public SchoolResponse updateSchool(@PathVariable Long id, @RequestBody @Valid SchoolRequest r) {
        var s = schools.findById(id).orElseThrow(() -> new NotFoundException("School not found"));
        apply(s, r);
        return map(schools.save(s));
    }

    @PostMapping("/academic-years")
    public AcademicYearResponse createYear(@RequestBody @Valid AcademicYearRequest r) {
        var y = new AcademicYear();
        y.setSchool(reqSchool(r.schoolId()));
        y.setName(r.name());
        y.setStartDate(r.startDate());
        y.setEndDate(r.endDate());
        return map(years.save(y));
    }

    @GetMapping("/schools/{schoolId}/academic-years")
    public Page<AcademicYearResponse> listYears(@PathVariable Long schoolId, Pageable p) {
        return years.findBySchoolId(schoolId, p).map(this::map);
    }

    @PutMapping("/academic-years/{id}/active")
    public AcademicYearResponse setActive(@PathVariable Long id, @RequestBody @Valid SetActiveAcademicYearRequest r) {
        var y = years.findById(id).orElseThrow(() -> new NotFoundException("Academic year not found"));
        y.setActive(true);
        years.findBySchoolId(r.schoolId(), Pageable.unpaged()).forEach(a -> {
            if (!a.getId().equals(id)) {
                a.setActive(false);
                years.save(a);
            }
        });
        return map(years.save(y));
    }

    @PostMapping("/terms")
    public TermResponse createTerm(@RequestBody @Valid TermRequest r) {
        var t = new Term();
        t.setSchool(reqSchool(r.schoolId()));
        t.setAcademicYear(years.findById(r.academicYearId()).orElseThrow(() -> new NotFoundException("Academic year not found")));
        t.setName(r.name());
        t.setStartDate(r.startDate());
        t.setEndDate(r.endDate());
        return map(terms.save(t));
    }

    @GetMapping("/academic-years/{academicYearId}/terms")
    public Page<TermResponse> listTerms(@PathVariable Long academicYearId, Pageable p) {
        return terms.findByAcademicYearId(academicYearId, p).map(this::map);
    }

    @PostMapping("/grades")
    public GradeResponse createGrade(@RequestBody @Valid GradeRequest r) {
        uniqueness.ensureUniqueGradeName(r.schoolId(), r.name(), null);
        var g = new Grade();
        g.setSchool(reqSchool(r.schoolId()));
        g.setName(r.name());
        return map(grades.save(g));
    }

    @PutMapping("/grades/{id}")
    public GradeResponse updateGrade(@PathVariable Long id, @RequestBody @Valid GradeRequest r) {
        var g = grades.findById(id).orElseThrow(() -> new NotFoundException("Grade not found"));
        uniqueness.ensureUniqueGradeName(r.schoolId(), r.name(), g.getId());
        g.setSchool(reqSchool(r.schoolId()));
        g.setName(r.name());
        return map(grades.save(g));
    }

    @GetMapping("/schools/{schoolId}/grades")
    public Page<GradeResponse> listGrades(@PathVariable Long schoolId, Pageable p) {
        return grades.findBySchoolId(schoolId, p).map(this::map);
    }

    @PostMapping("/classes")
    public ClassRoomResponse createClass(@RequestBody @Valid ClassRoomRequest r) {
        uniqueness.ensureUniqueClassCode(r.schoolId(), r.academicYearId(), r.code(), null);
        var c = new ClassRoom();
        c.setSchool(reqSchool(r.schoolId()));
        c.setAcademicYear(years.findById(r.academicYearId()).orElseThrow(() -> new NotFoundException("Academic year not found")));
        c.setGrade(grades.findById(r.gradeId()).orElseThrow(() -> new NotFoundException("Grade not found")));
        c.setCode(r.code());
        c.setName(r.name());
        return map(classes.save(c));
    }

    @PutMapping("/classes/{id}")
    public ClassRoomResponse updateClass(@PathVariable Long id, @RequestBody @Valid ClassRoomRequest r) {
        var c = classes.findById(id).orElseThrow(() -> new NotFoundException("Class not found"));
        uniqueness.ensureUniqueClassCode(r.schoolId(), r.academicYearId(), r.code(), c.getId());
        c.setSchool(reqSchool(r.schoolId()));
        c.setAcademicYear(years.findById(r.academicYearId()).orElseThrow(() -> new NotFoundException("Academic year not found")));
        c.setGrade(grades.findById(r.gradeId()).orElseThrow(() -> new NotFoundException("Grade not found")));
        c.setCode(r.code());
        c.setName(r.name());
        return map(classes.save(c));
    }

    @GetMapping("/schools/{schoolId}/academic-years/{academicYearId}/classes")
    public Page<ClassRoomResponse> listClasses(@PathVariable Long schoolId, @PathVariable Long academicYearId, Pageable p) {
        return classes.findBySchoolIdAndAcademicYearId(schoolId, academicYearId, p).map(this::map);
    }

    @PostMapping("/houses")
    public NamedResponse createHouse(@RequestBody @Valid NamedRequest r) {
        uniqueness.ensureUniqueHouseName(r.schoolId(), r.name(), null);
        var e = new House();
        e.setSchool(reqSchool(r.schoolId()));
        e.setName(r.name());
        return map(houses.save(e));
    }

    @GetMapping("/schools/{schoolId}/houses")
    public Page<NamedResponse> listHouses(@PathVariable Long schoolId, Pageable p) {
        return houses.findBySchoolId(schoolId, p).map(h -> new NamedResponse(h.getId(), schoolId, h.getName()));
    }

    @PutMapping("/houses/{id}")
    public NamedResponse updateHouse(@PathVariable Long id, @RequestBody @Valid NamedRequest r) {
        var e = houses.findById(id).orElseThrow(() -> new NotFoundException("House not found"));
        uniqueness.ensureUniqueHouseName(r.schoolId(), r.name(), e.getId());
        e.setSchool(reqSchool(r.schoolId()));
        e.setName(r.name());
        return map(houses.save(e));
    }

    @DeleteMapping("/houses/{id}")
    public void deleteHouse(@PathVariable Long id) {
        houses.deleteById(id);
    }

    @PostMapping("/extra-mural-types")
    public NamedResponse createType(@RequestBody @Valid NamedRequest r) { var e = new ExtraMuralType(); e.setSchool(reqSchool(r.schoolId())); e.setName(r.name()); return map(types.save(e)); }
    @GetMapping("/schools/{schoolId}/extra-mural-types")
    public Page<NamedResponse> listTypes(@PathVariable Long schoolId, Pageable p) { return types.findBySchoolId(schoolId, p).map(x -> new NamedResponse(x.getId(), schoolId, x.getName())); }
    @PutMapping("/extra-mural-types/{id}")
    public NamedResponse updateType(@PathVariable Long id, @RequestBody @Valid NamedRequest r) { var e = types.findById(id).orElseThrow(() -> new NotFoundException("Type not found")); e.setName(r.name()); return map(types.save(e)); }
    @DeleteMapping("/extra-mural-types/{id}")
    public void deleteType(@PathVariable Long id) { types.deleteById(id); }

    @PostMapping("/extra-mural-activities")
    public NamedResponse createActivity(@RequestBody @Valid ExtraMuralActivityRequest r) { var e = new ExtraMuralActivity(); e.setSchool(reqSchool(r.schoolId())); e.setType(types.findById(r.typeId()).orElseThrow(() -> new NotFoundException("Type not found"))); e.setName(r.name()); var s = acts.save(e); return new NamedResponse(s.getId(), r.schoolId(), s.getName()); }
    @GetMapping("/schools/{schoolId}/extra-mural-activities")
    public Page<NamedResponse> listActivities(@PathVariable Long schoolId, Pageable p) { return acts.findBySchoolId(schoolId, p).map(x -> new NamedResponse(x.getId(), schoolId, x.getName())); }
    @PutMapping("/extra-mural-activities/{id}")
    public NamedResponse updateActivity(@PathVariable Long id, @RequestBody @Valid ExtraMuralActivityRequest r) { var e = acts.findById(id).orElseThrow(() -> new NotFoundException("Activity not found")); e.setName(r.name()); e.setType(types.findById(r.typeId()).orElseThrow(() -> new NotFoundException("Type not found"))); var s = acts.save(e); return new NamedResponse(s.getId(), s.getSchool().getId(), s.getName()); }
    @DeleteMapping("/extra-mural-activities/{id}")
    public void deleteActivity(@PathVariable Long id) { acts.deleteById(id); }

    @PostMapping("/sports")
    public NamedResponse createSport(@RequestBody @Valid NamedRequest r) { var e = new Sport(); e.setSchool(reqSchool(r.schoolId())); e.setName(r.name()); var s = sports.save(e); return new NamedResponse(s.getId(), r.schoolId(), s.getName()); }
    @GetMapping("/schools/{schoolId}/sports")
    public Page<NamedResponse> listSports(@PathVariable Long schoolId, Pageable p) { return sports.findBySchoolId(schoolId, p).map(x -> new NamedResponse(x.getId(), schoolId, x.getName())); }
    @PutMapping("/sports/{id}")
    public NamedResponse updateSport(@PathVariable Long id, @RequestBody @Valid NamedRequest r) { var e = sports.findById(id).orElseThrow(() -> new NotFoundException("Sport not found")); e.setName(r.name()); var s = sports.save(e); return new NamedResponse(s.getId(), s.getSchool().getId(), s.getName()); }
    @DeleteMapping("/sports/{id}")
    public void deleteSport(@PathVariable Long id) { sports.deleteById(id); }

    @PostMapping("/teams")
    public NamedResponse createTeam(@RequestBody @Valid TeamRequest r) { var e = new Team(); e.setSchool(reqSchool(r.schoolId())); e.setSport(sports.findById(r.sportId()).orElseThrow(() -> new NotFoundException("Sport not found"))); e.setName(r.name()); var s = teams.save(e); return new NamedResponse(s.getId(), r.schoolId(), s.getName()); }
    @GetMapping("/schools/{schoolId}/teams")
    public Page<NamedResponse> listTeams(@PathVariable Long schoolId, Pageable p) { return teams.findBySchoolId(schoolId, p).map(x -> new NamedResponse(x.getId(), schoolId, x.getName())); }
    @PutMapping("/teams/{id}")
    public NamedResponse updateTeam(@PathVariable Long id, @RequestBody @Valid TeamRequest r) { var e = teams.findById(id).orElseThrow(() -> new NotFoundException("Team not found")); e.setName(r.name()); e.setSport(sports.findById(r.sportId()).orElseThrow(() -> new NotFoundException("Sport not found"))); var s = teams.save(e); return new NamedResponse(s.getId(), s.getSchool().getId(), s.getName()); }
    @DeleteMapping("/teams/{id}")
    public void deleteTeam(@PathVariable Long id) { teams.deleteById(id); }

    @PostMapping("/competitions")
    public NamedResponse createCompetition(@RequestBody @Valid CompetitionRequest r) { var e = new Competition(); e.setSchool(reqSchool(r.schoolId())); e.setSport(sports.findById(r.sportId()).orElseThrow(() -> new NotFoundException("Sport not found"))); e.setName(r.name()); var s = comps.save(e); return new NamedResponse(s.getId(), r.schoolId(), s.getName()); }
    @GetMapping("/schools/{schoolId}/competitions")
    public Page<NamedResponse> listCompetitions(@PathVariable Long schoolId, Pageable p) { return comps.findBySchoolId(schoolId, p).map(x -> new NamedResponse(x.getId(), schoolId, x.getName())); }
    @PutMapping("/competitions/{id}")
    public NamedResponse updateCompetition(@PathVariable Long id, @RequestBody @Valid CompetitionRequest r) { var e = comps.findById(id).orElseThrow(() -> new NotFoundException("Competition not found")); e.setName(r.name()); e.setSport(sports.findById(r.sportId()).orElseThrow(() -> new NotFoundException("Sport not found"))); var s = comps.save(e); return new NamedResponse(s.getId(), s.getSchool().getId(), s.getName()); }
    @DeleteMapping("/competitions/{id}")
    public void deleteCompetition(@PathVariable Long id) { comps.deleteById(id); }

    @PostMapping("/bus-routes")
    public NamedResponse createRoute(@RequestBody @Valid NamedRequest r) { var e = new BusRoute(); e.setSchool(reqSchool(r.schoolId())); e.setName(r.name()); var s = routes.save(e); return new NamedResponse(s.getId(), r.schoolId(), s.getName()); }
    @GetMapping("/schools/{schoolId}/bus-routes")
    public Page<NamedResponse> listRoutes(@PathVariable Long schoolId, Pageable p) { return routes.findBySchoolId(schoolId, p).map(x -> new NamedResponse(x.getId(), schoolId, x.getName())); }
    @PutMapping("/bus-routes/{id}")
    public NamedResponse updateRoute(@PathVariable Long id, @RequestBody @Valid NamedRequest r) { var e = routes.findById(id).orElseThrow(() -> new NotFoundException("Route not found")); e.setName(r.name()); var s = routes.save(e); return new NamedResponse(s.getId(), s.getSchool().getId(), s.getName()); }
    @DeleteMapping("/bus-routes/{id}")
    public void deleteRoute(@PathVariable Long id) { routes.deleteById(id); }

    @PostMapping("/bus-ticket-types")
    public NamedResponse createTicket(@RequestBody @Valid NamedRequest r) { var e = new BusTicketType(); e.setSchool(reqSchool(r.schoolId())); e.setName(r.name()); var s = tickets.save(e); return new NamedResponse(s.getId(), r.schoolId(), s.getName()); }
    @GetMapping("/schools/{schoolId}/bus-ticket-types")
    public Page<NamedResponse> listTickets(@PathVariable Long schoolId, Pageable p) { return tickets.findBySchoolId(schoolId, p).map(x -> new NamedResponse(x.getId(), schoolId, x.getName())); }
    @PutMapping("/bus-ticket-types/{id}")
    public NamedResponse updateTicket(@PathVariable Long id, @RequestBody @Valid NamedRequest r) { var e = tickets.findById(id).orElseThrow(() -> new NotFoundException("Ticket type not found")); e.setName(r.name()); var s = tickets.save(e); return new NamedResponse(s.getId(), s.getSchool().getId(), s.getName()); }
    @DeleteMapping("/bus-ticket-types/{id}")
    public void deleteTicket(@PathVariable Long id) { tickets.deleteById(id); }

    @PostMapping("/merit-codes")
    public CodeResponse createMerit(@RequestBody @Valid CodeRequest r) {
        uniqueness.ensureUniqueMeritCode(r.schoolId(), r.code(), null);
        var e = new MeritCode();
        e.setSchool(reqSchool(r.schoolId()));
        e.setCode(r.code());
        e.setDescription(r.description());
        var s = merits.save(e);
        return new CodeResponse(s.getId(), r.schoolId(), s.getCode(), s.getDescription());
    }

    @GetMapping("/schools/{schoolId}/merit-codes")
    public Page<CodeResponse> listMerits(@PathVariable Long schoolId, Pageable p) {
        return merits.findBySchoolId(schoolId, p).map(x -> new CodeResponse(x.getId(), schoolId, x.getCode(), x.getDescription()));
    }

    @PutMapping("/merit-codes/{id}")
    public CodeResponse updateMerit(@PathVariable Long id, @RequestBody @Valid CodeRequest r) {
        var e = merits.findById(id).orElseThrow(() -> new NotFoundException("Merit code not found"));
        uniqueness.ensureUniqueMeritCode(r.schoolId(), r.code(), e.getId());
        e.setSchool(reqSchool(r.schoolId()));
        e.setCode(r.code());
        e.setDescription(r.description());
        var s = merits.save(e);
        return new CodeResponse(s.getId(), s.getSchool().getId(), s.getCode(), s.getDescription());
    }

    @DeleteMapping("/merit-codes/{id}")
    public void deleteMerit(@PathVariable Long id) {
        merits.deleteById(id);
    }

    @PostMapping("/demerit-codes")
    public CodeResponse createDemerit(@RequestBody @Valid CodeRequest r) {
        uniqueness.ensureUniqueDemeritCode(r.schoolId(), r.code(), null);
        var e = new DemeritCode();
        e.setSchool(reqSchool(r.schoolId()));
        e.setCode(r.code());
        e.setDescription(r.description());
        var s = demerits.save(e);
        return new CodeResponse(s.getId(), r.schoolId(), s.getCode(), s.getDescription());
    }

    @GetMapping("/schools/{schoolId}/demerit-codes")
    public Page<CodeResponse> listDemerits(@PathVariable Long schoolId, Pageable p) {
        return demerits.findBySchoolId(schoolId, p).map(x -> new CodeResponse(x.getId(), schoolId, x.getCode(), x.getDescription()));
    }

    @PutMapping("/demerit-codes/{id}")
    public CodeResponse updateDemerit(@PathVariable Long id, @RequestBody @Valid CodeRequest r) {
        var e = demerits.findById(id).orElseThrow(() -> new NotFoundException("Demerit code not found"));
        uniqueness.ensureUniqueDemeritCode(r.schoolId(), r.code(), e.getId());
        e.setSchool(reqSchool(r.schoolId()));
        e.setCode(r.code());
        e.setDescription(r.description());
        var s = demerits.save(e);
        return new CodeResponse(s.getId(), s.getSchool().getId(), s.getCode(), s.getDescription());
    }

    @DeleteMapping("/demerit-codes/{id}")
    public void deleteDemerit(@PathVariable Long id) {
        demerits.deleteById(id);
    }

    @PostMapping("/hostels")
    public NamedResponse createHostel(@RequestBody @Valid NamedRequest r) { var e = new Hostel(); e.setSchool(reqSchool(r.schoolId())); e.setName(r.name()); var s = hostels.save(e); return new NamedResponse(s.getId(), r.schoolId(), s.getName()); }
    @GetMapping("/schools/{schoolId}/hostels")
    public Page<NamedResponse> listHostels(@PathVariable Long schoolId, Pageable p) { return hostels.findBySchoolId(schoolId, p).map(x -> new NamedResponse(x.getId(), schoolId, x.getName())); }
    @PutMapping("/hostels/{id}")
    public NamedResponse updateHostel(@PathVariable Long id, @RequestBody @Valid NamedRequest r) { var e = hostels.findById(id).orElseThrow(() -> new NotFoundException("Hostel not found")); e.setName(r.name()); var s = hostels.save(e); return new NamedResponse(s.getId(), s.getSchool().getId(), s.getName()); }
    @DeleteMapping("/hostels/{id}")
    public void deleteHostel(@PathVariable Long id) { hostels.deleteById(id); }

    private School reqSchool(Long id) {
        return schools.findById(id).orElseThrow(() -> new NotFoundException("School not found"));
    }

    private void apply(School s, SchoolRequest r) {
        s.setName(r.name());
        s.setAddress(r.address());
        s.setContactEmail(r.contactEmail());
        s.setContactPhone(r.contactPhone());
        s.setLogoUrl(r.logoUrl());
        s.setDocumentHeader(r.documentHeader());
        s.setReportLanguages(r.reportLanguages());
        s.setDefaultTermStructure(r.defaultTermStructure());
        s.setDocumentNumberFormat(r.documentNumberFormat());
    }

    private SchoolResponse map(School s) { return new SchoolResponse(s.getId(), s.getName(), s.getAddress(), s.getContactEmail(), s.getContactPhone(), s.getLogoUrl(), s.getDocumentHeader(), s.getReportLanguages(), s.getDefaultTermStructure(), s.getDocumentNumberFormat()); }
    private AcademicYearResponse map(AcademicYear a) { return new AcademicYearResponse(a.getId(), a.getSchool().getId(), a.getName(), a.getStartDate(), a.getEndDate(), a.isActive()); }
    private TermResponse map(Term t) { return new TermResponse(t.getId(), t.getSchool().getId(), t.getAcademicYear().getId(), t.getName(), t.getStartDate(), t.getEndDate()); }
    private GradeResponse map(Grade g) { return new GradeResponse(g.getId(), g.getSchool().getId(), g.getName()); }
    private ClassRoomResponse map(ClassRoom c) { return new ClassRoomResponse(c.getId(), c.getSchool().getId(), c.getAcademicYear().getId(), c.getGrade().getId(), c.getCode(), c.getName()); }
    private NamedResponse map(House h) { return new NamedResponse(h.getId(), h.getSchool().getId(), h.getName()); }
    private NamedResponse map(ExtraMuralType h) { return new NamedResponse(h.getId(), h.getSchool().getId(), h.getName()); }
}
