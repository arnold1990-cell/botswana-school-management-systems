package com.bosams.schoolsetup.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "grade")
public class Grade extends BaseEntity {

    @Column(nullable = false, unique = true, length = 60)
    private String name;

    @Column(nullable = false)
    private Integer sortOrder;
}
