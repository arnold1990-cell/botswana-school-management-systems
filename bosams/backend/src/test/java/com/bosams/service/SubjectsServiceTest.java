package com.bosams.service;

import com.bosams.domain.Enums;
import com.bosams.domain.SubjectEntity;
import com.bosams.repository.SubjectRepository;
import com.bosams.web.ApiException;
import com.bosams.web.dto.SubjectDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectsServiceTest {
    @Mock SubjectRepository subjects;

    @InjectMocks SubjectsService service;

    @Test
    void getAllSubjectsReturnsActiveRows() {
        when(subjects.findByStatusOrderBySchoolLevelAscGradeFromAscNameAsc(Enums.EntityStatus.ACTIVE))
                .thenReturn(List.of(subject(1L, "English", Enums.SchoolLevel.PRIMARY, 1, 4)));

        List<SubjectDto.SubjectResponse> found = service.listSubjects(null, null);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).name()).isEqualTo("English");
        verify(subjects).findByStatusOrderBySchoolLevelAscGradeFromAscNameAsc(Enums.EntityStatus.ACTIVE);
    }

    @Test
    void filterByLevelReturnsMatchingRows() {
        when(subjects.findBySchoolLevelAndStatusOrderByGradeFromAscNameAsc(Enums.SchoolLevel.PRIMARY, Enums.EntityStatus.ACTIVE))
                .thenReturn(List.of(subject(2L, "Mathematics", Enums.SchoolLevel.PRIMARY, 5, 7)));

        List<SubjectDto.SubjectResponse> found = service.listSubjects(Enums.SchoolLevel.PRIMARY, null);

        assertThat(found).extracting(SubjectDto.SubjectResponse::level).containsOnly(Enums.SchoolLevel.PRIMARY);
        verify(subjects).findBySchoolLevelAndStatusOrderByGradeFromAscNameAsc(Enums.SchoolLevel.PRIMARY, Enums.EntityStatus.ACTIVE);
    }

    @Test
    void filterByLevelAndGradeUsesRangeQuery() {
        when(subjects.findBySchoolLevelAndStatusAndGradeFromLessThanEqualAndGradeToGreaterThanEqualOrderByGradeFromAscNameAsc(
                Enums.SchoolLevel.JUNIOR_SECONDARY,
                Enums.EntityStatus.ACTIVE,
                8,
                8
        )).thenReturn(List.of(subject(3L, "Integrated Science", Enums.SchoolLevel.JUNIOR_SECONDARY, 8, 10)));

        List<SubjectDto.SubjectResponse> found = service.listSubjects(Enums.SchoolLevel.JUNIOR_SECONDARY, 8);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).gradeFrom()).isEqualTo(8);
    }

    @Test
    void invalidGradeReturnsBadRequest() {
        assertThatThrownBy(() -> service.listSubjects(null, 0))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("grade must be at least 1");
    }

    private SubjectEntity subject(Long id, String name, Enums.SchoolLevel level, int gradeFrom, int gradeTo) {
        SubjectEntity subject = new SubjectEntity();
        subject.setId(id);
        subject.setName(name);
        subject.setCode(name.toUpperCase().replace(' ', '_'));
        subject.setSchoolLevel(level);
        subject.setGradeFrom(gradeFrom);
        subject.setGradeTo(gradeTo);
        subject.setElective(false);
        subject.setStatus(Enums.EntityStatus.ACTIVE);
        return subject;
    }
}
