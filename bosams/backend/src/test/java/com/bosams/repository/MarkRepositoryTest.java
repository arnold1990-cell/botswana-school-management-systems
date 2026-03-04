package com.bosams.repository;

import com.bosams.domain.*;
import com.bosams.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
class MarkRepositoryTest {
    @Autowired AcademicYearRepository years;
    @Autowired StandardRepository standards;
    @Autowired StreamRepository streams;
    @Autowired SubjectRepository subjects;
    @Autowired StudentRepository students;
    @Autowired ExamGroupRepository groups;
    @Autowired ExamScheduleRepository schedules;
    @Autowired MarkRepository marks;

    @Test
    void findersReturnExpectedMark() {
        AcademicYear y = years.save(TestDataFactory.academicYear(null, 2024, true));
        StandardEntity std = new StandardEntity(); std.setName("Grade 4"); std = standards.save(std);
        StreamEntity stream = new StreamEntity(); stream.setName("A"); stream.setStandard(std); stream = streams.save(stream);
        SubjectEntity subject = subjects.save(TestDataFactory.subject(null, "Math"));
        StudentEntity student = TestDataFactory.learner(null, 4); student.setAdmissionNo("ADM401"); student.setStream(stream); student = students.save(student);
        ExamGroup group = new ExamGroup(); group.setName("Mid"); group.setAcademicYear(y); group = groups.save(group);
        ExamSchedule schedule = new ExamSchedule();
        schedule.setExamGroup(group); schedule.setStream(stream); schedule.setSubject(subject); schedule.setExamDatetime(LocalDateTime.now());
        schedule.setMaxMarks(BigDecimal.valueOf(100)); schedule.setMarkEntryLastDate(LocalDate.now().plusDays(1));
        schedule = schedules.save(schedule);
        MarkEntity mark = new MarkEntity(); mark.setExamSchedule(schedule); mark.setStudent(student); mark.setMarks(BigDecimal.valueOf(77));
        marks.save(mark);

        assertThat(marks.findByExamScheduleId(schedule.getId())).hasSize(1);
        assertThat(marks.findByExamScheduleIdAndStudentId(schedule.getId(), student.getId())).isPresent();
    }
}
