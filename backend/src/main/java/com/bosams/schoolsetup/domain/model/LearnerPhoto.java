package com.bosams.schoolsetup.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "learner_photo")
public class LearnerPhoto extends BaseEntity {

    @Column(nullable = false)
    private UUID learnerId;

    @Column(nullable = false, length = 255)
    private String fileKey;

    @Column(nullable = false, length = 255)
    private String filename;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private long size;
}
