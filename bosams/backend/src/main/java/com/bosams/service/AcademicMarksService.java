package com.bosams.service;

import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.*;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class AcademicMarksService {
    private static final Logger log = LoggerFactory.getLogger(AcademicMarksService.class);
    private final StudentRepository learners;
    private final SubjectRepository subjects;
    private final AssessmentTaskRepository tasks;
    private final MarkEntryRepository markEntries;
    private final GradeCalculationService gradeCalculationService;
    private final UserRepository users;
    private final AuthorizationService authorizationService;

    public AcademicMarksService(StudentRepository learners, SubjectRepository subjects, AssessmentTaskRepository tasks, MarkEntryRepository markEntries, GradeCalculationService gradeCalculationService, UserRepository users, AuthorizationService authorizationService) {
        this.learners = learners;
        this.subjects = subjects;
        this.tasks = tasks;
        this.markEntries = markEntries;
        this.gradeCalculationService = gradeCalculationService;
        this.users = users;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public List<MarkEntryEntity> bulkSave(UUID userId, BulkMarkRequest request) {
        UserEntity actor = users.findById(userId).orElseThrow();
        log.info("Bulk marks save userId={} role={} subjectId={} taskId={} rows={}", actor.getId(), actor.getRole(), request.subjectId(), request.taskId(), request.marks().size());
        SubjectEntity subject = subjects.findById(request.subjectId()).orElseThrow();
        AssessmentTaskEntity task = tasks.findById(request.taskId()).orElseThrow();

        List<MarkEntryEntity> saved = new ArrayList<>();
        for (BulkLearnerMark m : request.marks()) {
            if (m.score() < 0 || m.score() > 50) {
                throw new ValidationException("Score must be between 0 and 50");
            }
            StudentEntity learner = learners.findById(m.learnerId()).orElseThrow();
            validateTeacherAccess(actor, learner, subject);
            MarkEntryEntity entity = markEntries.findByLearnerIdAndSubjectIdAndTaskId(learner.getId(), subject.getId(), task.getId()).orElseGet(MarkEntryEntity::new);
            if (entity.getId() != null && entity.getStatus() == Enums.MarkEntryStatus.SUBMITTED) {
                throw new ApiException(HttpStatus.CONFLICT, "MARKS_LOCKED", "Marks have been submitted and are locked");
            }
            entity.setLearner(learner);
            entity.setSubject(subject);
            entity.setTask(task);
            entity.setScore(m.score());
            entity.setGradeLetter(gradeCalculationService.calculate(m.score()));
            entity.setRecordedAt(Instant.now());
            entity.setRecordedByUserId(userId);
            entity.setStatus(Enums.MarkEntryStatus.DRAFT);
            entity.setSubmittedAt(null);
            entity.setSubmittedByUserId(null);
            saved.add(markEntries.save(entity));
        }
        return saved;
    }



    public List<MarkEntryEntity> list(UserEntity actor, Long subjectId, Long taskId, Integer gradeLevel) {
        if (authorizationService.isTeacher(actor)) {
            authorizationService.enforceTeacherAssignment(actor, gradeLevel, subjectId);
        }
        List<MarkEntryEntity> entries = markEntries.findByTaskIdAndSubjectId(taskId, subjectId).stream()
                .filter(e -> Objects.equals(e.getLearner().getGradeLevel(), gradeLevel))
                .toList();
        log.info("Marks list userId={} role={} subjectId={} taskId={} gradeLevel={} count={}", actor.getId(), actor.getRole(), subjectId, taskId, gradeLevel, entries.size());
        return entries;
    }

    @Transactional
    public StatusResponse submit(UUID userId, Long subjectId, Long taskId, Integer gradeLevel) {
        UserEntity actor = users.findById(userId).orElseThrow();
        if (!authorizationService.isTeacher(actor)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Only teachers can submit marks");
        }
        Long activeYearId = authorizationService.getActiveAcademicYear().getId();
        if (!authorizationService.teacherHasAssignment(actor.getId(), activeYearId, gradeLevel, subjectId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_ASSIGNED", "Teacher not assigned for grade/subject");
        }

        List<MarkEntryEntity> entries = markEntries.findByTaskIdAndSubjectId(taskId, subjectId).stream()
                .filter(e -> Objects.equals(e.getLearner().getGradeLevel(), gradeLevel))
                .toList();

        entries.forEach(e -> {
            e.setStatus(Enums.MarkEntryStatus.SUBMITTED);
            e.setSubmittedAt(Instant.now());
            e.setSubmittedByUserId(userId);
        });
        markEntries.saveAll(entries);
        return new StatusResponse(entries.isEmpty() ? Enums.MarkEntryStatus.DRAFT.name() : Enums.MarkEntryStatus.SUBMITTED.name(), entries.size());
    }

    @Transactional
    public StatusResponse unlock(UUID userId, Long subjectId, Long taskId, Integer gradeLevel) {
        UserEntity actor = users.findById(userId).orElseThrow();
        if (!(authorizationService.isAdmin(actor) || authorizationService.isPrincipal(actor))) {
            throw new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", "Only admin/principal can unlock");
        }
        List<MarkEntryEntity> entries = markEntries.findByTaskIdAndSubjectId(taskId, subjectId).stream()
                .filter(e -> Objects.equals(e.getLearner().getGradeLevel(), gradeLevel))
                .toList();

        entries.forEach(e -> {
            e.setStatus(Enums.MarkEntryStatus.DRAFT);
            e.setSubmittedAt(null);
            e.setSubmittedByUserId(null);
        });
        markEntries.saveAll(entries);
        return new StatusResponse(Enums.MarkEntryStatus.DRAFT.name(), entries.size());
    }

    public StatusResponse status(Long subjectId, Long taskId, Integer gradeLevel) {
        List<MarkEntryEntity> entries = markEntries.findByTaskIdAndSubjectId(taskId, subjectId).stream()
                .filter(e -> Objects.equals(e.getLearner().getGradeLevel(), gradeLevel))
                .toList();
        boolean anySubmitted = entries.stream().anyMatch(e -> e.getStatus() == Enums.MarkEntryStatus.SUBMITTED);
        return new StatusResponse(anySubmitted ? Enums.MarkEntryStatus.SUBMITTED.name() : Enums.MarkEntryStatus.DRAFT.name(), entries.size());
    }

    private void validateTeacherAccess(UserEntity actor, StudentEntity learner, SubjectEntity subject) {
        if (!authorizationService.isTeacher(actor)) {
            return;
        }
        Long activeYearId = authorizationService.getActiveAcademicYear().getId();
        if (!authorizationService.teacherHasAssignment(actor.getId(), activeYearId, learner.getGradeLevel(), subject.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_ASSIGNED", "Teacher not assigned for learner grade/subject");
        }
    }

    public List<TermReportRow> termReport(Integer year, Integer termNumber, Integer gradeLevel, Long subjectId) {
        AssessmentTaskEntity catTask = tasks.findByTermAcademicYearYearAndTermTermNoAndType(year, termNumber, Enums.AssessmentType.CAT)
                .orElseThrow(() -> new ValidationException("CAT task not found"));
        AssessmentTaskEntity examTask = tasks.findByTermAcademicYearYearAndTermTermNoAndType(year, termNumber, Enums.AssessmentType.EXAM)
                .orElseThrow(() -> new ValidationException("EXAM task not found"));

        List<StudentEntity> gradeLearners = learners.findByGradeLevel(gradeLevel);
        Map<Long, Integer> catScores = markEntries.findByTaskIdAndSubjectId(catTask.getId(), subjectId)
                .stream().collect(HashMap::new, (map, e) -> map.put(e.getLearner().getId(), e.getScore()), HashMap::putAll);
        Map<Long, Integer> examScores = markEntries.findByTaskIdAndSubjectId(examTask.getId(), subjectId)
                .stream().collect(HashMap::new, (map, e) -> map.put(e.getLearner().getId(), e.getScore()), HashMap::putAll);

        return gradeLearners.stream().map(l -> {
            Integer cat = catScores.get(l.getId());
            Integer exam = examScores.get(l.getId());
            int total = (cat == null ? 0 : cat) + (exam == null ? 0 : exam);
            Enums.GradeLetter grade = gradeCalculationService.calculate(Math.min(total / 2, 50));
            return new TermReportRow(l.getId(), l.getAdmissionNo(), l.getFirstName() + " " + l.getLastName(), cat, exam, total, grade.name());
        }).toList();
    }

    public record BulkMarkRequest(Long subjectId, Long taskId, List<BulkLearnerMark> marks) {}
    public record BulkLearnerMark(Long learnerId, Integer score) {}
    public record TermReportRow(Long learnerId, String admissionNo, String learnerName, Integer catScore, Integer examScore, Integer total, String finalGrade) {}
    public record StatusResponse(String status, int rows) {}
}
