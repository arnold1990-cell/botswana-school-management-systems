package com.bosams.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "grade_profile")
@Getter @Setter @NoArgsConstructor
public class GradeProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(name = "rules_json", nullable = false, columnDefinition = "text") private String rulesJson;
    @Column(name = "is_default", nullable = false) private boolean isDefault;
}
