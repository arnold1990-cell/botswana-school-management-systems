package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity @Table(name="demerit_codes", uniqueConstraints=@UniqueConstraint(columnNames={"school_id","code"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemeritCode extends SchoolOwnedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,length=40) private String code; @Column(nullable=false,length=120) private String description;

}
