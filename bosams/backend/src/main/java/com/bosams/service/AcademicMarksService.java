package com.bosams.service;

import com.bosams.domain.*;
import com.bosams.repository.*;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class AcademicMarksService {
    private final StudentRepository learners;
    private final SubjectRepository subjects;
    private final AssessmentTaskRepository tasks;
    private final MarkEntryRepository markEntries;
    private final GradeCalculationService gradeCalculationService;

    public AcademicMarksService(StudentRepository learners, SubjectRepository subjects, AssessmentTaskRepository tasks, MarkEntryRepository markEntries, GradeCalculationService gradeCalculationService) {
        this.learners = learners;
        this.subjects = subjects;
        this.tasks = tasks;
        this.markEntries = markEntries;
        this.gradeCalculationService = gradeCalculationService;
    }

    @Transactional
    public List<MarkEntryEntity> bulkSave(UUID userId, BulkMarkRequest request) {
        SubjectEntity subject = subjects.findById(request.subjectId()).orElseThrow();
        AssessmentTaskEntity task = tasks.findById(request.taskId()).orElseThrow();
        List<MarkEntryEntity> saved = new ArrayList<>();
        for (BulkLearnerMark m : request.marks()) {
            if (m.score() < 0 || m.score() > 50) {
                throw new ValidationException("Score must be between 0 and 50");
            }
            StudentEntity learner = learners.findById(m.learnerId()).orElseThrow();
            MarkEntryEntity entity = markEntries.findByLearnerIdAndSubjectIdAndTaskId(learner.getId(), subject.getId(), task.getId()).orElseGet(MarkEntryEntity::new);
            entity.setLearner(learner);
            entity.setSubject(subject);
            entity.setTask(task);
            entity.setScore(m.score());
            entity.setGradeLetter(gradeCalculationService.calculate(m.score()));
            entity.setRecordedAt(Instant.now());
            entity.setRecordedByUserId(userId);
            saved.add(markEntries.save(entity));
        }
        return saved;
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
}
