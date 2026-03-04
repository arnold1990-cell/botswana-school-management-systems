package com.bosams.repository;

import com.bosams.domain.Enums;
import com.bosams.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
class AssessmentTaskRepositoryTest {
    @Autowired AcademicYearRepository yearRepository;
    @Autowired TermRepository termRepository;
    @Autowired AssessmentTaskRepository taskRepository;

    @Test
    void findsTaskByTermAndType() {
        var year = yearRepository.save(TestDataFactory.academicYear(null, 2024, true));
        var term = termRepository.save(TestDataFactory.term(null, year, 1));
        var cat = new com.bosams.domain.AssessmentTaskEntity();
        cat.setTerm(term); cat.setType(Enums.AssessmentType.CAT); cat.setMaxScore(50);
        taskRepository.save(cat);

        assertThat(taskRepository.findByTermIdAndType(term.getId(), Enums.AssessmentType.CAT)).isPresent();
    }
}
