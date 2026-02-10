package com.bosams.schoolsetup.domain.model;

import com.bosams.schoolsetup.domain.enums.SchoolType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "school_profile")
public class SchoolProfile extends BaseEntity {

    @Column(nullable = false, length = 180)
    private String name;

    @Column(nullable = false, length = 300)
    private String address;

    @Column(length = 30)
    private String phone;

    @Column(length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SchoolType schoolType;

    @Column(length = 255)
    private String logoFileKey;

    @Column(length = 255)
    private String logoFilename;

    @Column(length = 100)
    private String logoContentType;

    private Long logoSize;
}
