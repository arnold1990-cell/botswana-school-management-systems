package com.bosams.schoolsetup.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractMasterDataEntity extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 50)
    private String code;

    @Column(nullable = false)
    private boolean active = true;
}
