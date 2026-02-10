package com.bosams.schoolsetup.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;

@MappedSuperclass
public abstract class SchoolOwnedEntity extends AuditableEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
    public School getSchool(){return school;} public void setSchool(School school){this.school=school;}
}
