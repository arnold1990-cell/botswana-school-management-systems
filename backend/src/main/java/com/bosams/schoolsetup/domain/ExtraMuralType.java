package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;
@Entity @Table(name="extra_mural_types")
public class ExtraMuralType extends SchoolOwnedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,length=100) private String name; public Long getId(){return id;} public String getName(){return name;} public void setName(String n){name=n;} }
