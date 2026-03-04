package com.bosams.service;

import com.bosams.domain.Enums;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GradeCalculationServiceTest {
    private final GradeCalculationService service = new GradeCalculationService();

    @Test
    void calculateReturnsAFor40To50() {
        assertThat(service.calculate(40)).isEqualTo(Enums.GradeLetter.A);
        assertThat(service.calculate(50)).isEqualTo(Enums.GradeLetter.A);
    }

    @Test
    void calculateReturnsBFor35To39() {
        assertThat(service.calculate(37)).isEqualTo(Enums.GradeLetter.B);
    }

    @Test
    void calculateReturnsFForLowerRange() {
        assertThat(service.calculate(0)).isEqualTo(Enums.GradeLetter.F);
        assertThat(service.calculate(19)).isEqualTo(Enums.GradeLetter.F);
    }

    @Test
    void calculateThrowsForInvalidAboveRange() {
        assertThatThrownBy(() -> service.calculate(51)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void calculateThrowsForInvalidNegativeRange() {
        assertThatThrownBy(() -> service.calculate(-1)).isInstanceOf(RuntimeException.class);
    }
}
