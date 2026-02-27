package com.bosams.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "stream")
@Getter @Setter @NoArgsConstructor
public class StreamEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "standard_id") private StandardEntity standard;
    @Column(nullable = false) private String name;
}
