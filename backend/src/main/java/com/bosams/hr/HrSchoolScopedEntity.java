package com.bosams.hr;

import com.bosams.schoolsetup.domain.SchoolOwnedEntity;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class HrSchoolScopedEntity extends SchoolOwnedEntity {
}
