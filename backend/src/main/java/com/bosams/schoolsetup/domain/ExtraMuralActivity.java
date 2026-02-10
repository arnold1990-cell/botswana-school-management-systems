package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity @Table(name="extra_mural_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtraMuralActivity extends SchoolOwnedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,length=100) private String name; @ManyToOne(optional=false) @JoinColumn(name="type_id") private ExtraMuralType type;

}
