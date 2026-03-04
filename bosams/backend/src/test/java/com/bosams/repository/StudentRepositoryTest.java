package com.bosams.repository;

import com.bosams.domain.Enums;
import com.bosams.domain.StandardEntity;
import com.bosams.domain.StreamEntity;
import com.bosams.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
class StudentRepositoryTest {
    @Autowired StandardRepository standardRepository;
    @Autowired StreamRepository streamRepository;
    @Autowired StudentRepository studentRepository;

    @Test
    void findByGradeLevelAndStatusFiltersCorrectly() {
        StandardEntity standard = new StandardEntity(); standard.setName("Grade 5"); standard = standardRepository.save(standard);
        StreamEntity stream = new StreamEntity(); stream.setName("A"); stream.setStandard(standard); stream = streamRepository.save(stream);
        var active = TestDataFactory.learner(null, 5); active.setAdmissionNo("A1"); active.setStream(stream); active.setStatus(Enums.EntityStatus.ACTIVE);
        var inactive = TestDataFactory.learner(null, 5); inactive.setAdmissionNo("A2"); inactive.setStream(stream); inactive.setStatus(Enums.EntityStatus.INACTIVE);
        studentRepository.save(active); studentRepository.save(inactive);

        assertThat(studentRepository.findByGradeLevelAndStatus(5, Enums.EntityStatus.ACTIVE)).hasSize(1);
        assertThat(studentRepository.findByStreamId(stream.getId())).hasSize(2);
    }
}
