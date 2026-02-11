package com.bosams.learnerparent.domain;

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
abstract class SchoolScopedEntity extends SchoolOwnedEntity {
}
