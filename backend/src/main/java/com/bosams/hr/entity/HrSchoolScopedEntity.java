package com.bosams.hr.entity;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public abstract class HrSchoolScopedEntity extends AuditableEntity {

    @Column(name = "school_id", nullable = false)
    private Long schoolId;
}
