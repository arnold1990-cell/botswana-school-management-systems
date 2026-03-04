package com.bosams.service;

import com.bosams.domain.AcademicYear;
import com.bosams.domain.Term;
import com.bosams.repository.AcademicYearRepository;
import com.bosams.repository.AssessmentTaskRepository;
import com.bosams.repository.TermRepository;
import com.bosams.testutil.TestDataFactory;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcademicsServiceTest {

    @Mock AcademicYearRepository academicYears;
    @Mock TermRepository terms;
    @Mock AssessmentTaskRepository tasks;

    @InjectMocks AcademicsService service;

    @Test
    void createAcademicYearCreatesAndSeeds() {
        when(academicYears.findByYear(2026)).thenReturn(Optional.empty());
        when(academicYears.findByActiveTrue()).thenReturn(Optional.empty());
        when(academicYears.save(any(AcademicYear.class))).thenAnswer(i -> {
            AcademicYear ay = i.getArgument(0);
            if (ay.getId() == null) ay.setId(10L);
            return ay;
        });
        when(terms.save(any(Term.class))).thenAnswer(i -> {
            Term term = i.getArgument(0);
            term.setId((long) term.getTermNo());
            return term;
        });

        AcademicYear out = service.createAcademicYear(2026);

        assertThat(out.getYear()).isEqualTo(2026);
        verify(academicYears, times(1)).save(any(AcademicYear.class));
        verify(terms, times(3)).save(any(Term.class));
        verify(tasks, times(6)).save(any());
    }

    @Test
    void createAcademicYearThrowsWhenDuplicate() {
        when(academicYears.findByYear(2025)).thenReturn(Optional.of(TestDataFactory.academicYear(1L, 2025, true)));

        assertThatThrownBy(() -> service.createAcademicYear(2025)).isInstanceOf(ValidationException.class);
        verify(terms, never()).save(any());
    }

    @Test
    void ensureActiveYearSetupUsesExistingYear() {
        AcademicYear active = TestDataFactory.academicYear(1L, 2024, true);
        when(academicYears.findByActiveTrue()).thenReturn(Optional.of(active));
        when(terms.findByAcademicYearYearOrderByTermNo(2024)).thenReturn(List.of(new Term(), new Term(), new Term()));

        AcademicYear out = service.ensureActiveYearSetup();

        assertThat(out.getYear()).isEqualTo(2024);
        verify(terms, never()).findByAcademicYearYearAndTermNo(anyInt(), anyInt());
    }

    @Test
    void ensureTermsAndTasksCreatesMissingTermAndTask() {
        AcademicYear ay = TestDataFactory.academicYear(2L, 2027, true);
        when(terms.findByAcademicYearYearAndTermNo(eq(2027), anyInt())).thenReturn(Optional.empty());
        when(terms.save(any(Term.class))).thenAnswer(i -> {
            Term t = i.getArgument(0);
            t.setId((long) t.getTermNo());
            return t;
        });
        when(tasks.findByTermIdAndType(anyLong(), any())).thenReturn(Optional.empty());

        service.ensureTermsAndTasks(ay);

        verify(terms, times(3)).save(any(Term.class));
        verify(tasks, times(6)).save(any());
    }

    @Test
    void ensureTermsAndTasksSkipsExistingTask() {
        AcademicYear ay = TestDataFactory.academicYear(3L, 2028, true);
        Term t = TestDataFactory.term(1L, ay, 1);
        when(terms.findByAcademicYearYearAndTermNo(2028, 1)).thenReturn(Optional.of(t));
        when(terms.findByAcademicYearYearAndTermNo(2028, 2)).thenReturn(Optional.of(TestDataFactory.term(2L, ay, 2)));
        when(terms.findByAcademicYearYearAndTermNo(2028, 3)).thenReturn(Optional.of(TestDataFactory.term(3L, ay, 3)));
        when(tasks.findByTermIdAndType(anyLong(), any())).thenReturn(Optional.of(new com.bosams.domain.AssessmentTaskEntity()));

        service.ensureTermsAndTasks(ay);

        verify(tasks, never()).save(any());
    }
}
