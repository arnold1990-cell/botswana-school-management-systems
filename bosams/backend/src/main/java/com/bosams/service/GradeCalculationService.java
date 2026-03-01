package com.bosams.service;

import com.bosams.domain.Enums;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeCalculationService {
    private final List<Rule> rules = List.of(
            new Rule(40, 50, Enums.GradeLetter.A),
            new Rule(35, 39, Enums.GradeLetter.B),
            new Rule(30, 34, Enums.GradeLetter.C),
            new Rule(25, 29, Enums.GradeLetter.D),
            new Rule(20, 24, Enums.GradeLetter.E),
            new Rule(0, 19, Enums.GradeLetter.F)
    );

    public Enums.GradeLetter calculate(int score) {
        return rules.stream().filter(r -> score >= r.min && score <= r.max).findFirst().orElseThrow().grade;
    }

    private record Rule(int min, int max, Enums.GradeLetter grade) {}
}
