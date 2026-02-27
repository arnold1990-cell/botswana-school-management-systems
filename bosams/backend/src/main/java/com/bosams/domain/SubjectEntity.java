package com.bosams.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "subject")
@Getter @Setter @NoArgsConstructor
public class SubjectEntity { @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(nullable = false, unique = true) private String name; }
