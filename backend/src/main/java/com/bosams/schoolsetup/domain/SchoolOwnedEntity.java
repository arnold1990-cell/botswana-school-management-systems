package com.bosams.schoolsetup.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class SchoolOwnedEntity extends AuditableEntity {
    @Column(name = "school_id", nullable = false)
    private Long schoolId;

}
