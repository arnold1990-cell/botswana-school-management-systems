package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;
@Entity @Table(name="extra_mural_activities")
public class ExtraMuralActivity extends SchoolOwnedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,length=100) private String name; @ManyToOne(optional=false) @JoinColumn(name="type_id") private ExtraMuralType type; public Long getId(){return id;} public String getName(){return name;} public void setName(String n){name=n;} public ExtraMuralType getType(){return type;} public void setType(ExtraMuralType t){type=t;} }
