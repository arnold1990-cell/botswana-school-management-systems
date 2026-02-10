package com.bosams.schoolsetup.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "schools")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(length = 255)
    private String address;
    @Column(length = 100)
    private String contactEmail;
    @Column(length = 40)
    private String contactPhone;
    @Column(length = 255)
    private String logoUrl;
    @Column(length = 255)
    private String documentHeader;
    @Column(length = 100)
    private String reportLanguages;
    @Column(length = 100)
    private String defaultTermStructure;
    @Column(length = 100)
    private String documentNumberFormat;

}
