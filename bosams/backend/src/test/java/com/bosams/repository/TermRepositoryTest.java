package com.bosams.repository;

import com.bosams.domain.AcademicYear;
import com.bosams.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
class TermRepositoryTest {
    @Autowired AcademicYearRepository yearRepository;
    @Autowired TermRepository termRepository;

    @Test
    void findByAcademicYearYearOrderByTermNoOrdersAscending() {
        AcademicYear year = yearRepository.save(TestDataFactory.academicYear(null, 2025, true));
        termRepository.save(TestDataFactory.term(null, year, 3));
        termRepository.save(TestDataFactory.term(null, year, 1));
        termRepository.save(TestDataFactory.term(null, year, 2));

        assertThat(termRepository.findByAcademicYearYearOrderByTermNo(2025)).extracting("termNo").containsExactly(1,2,3);
    }
}
