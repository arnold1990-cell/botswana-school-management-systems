package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;
@Entity @Table(name="teams")
public class Team extends SchoolOwnedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,length=100) private String name; @ManyToOne(optional=false) @JoinColumn(name="sport_id") private Sport sport; public Long getId(){return id;} public String getName(){return name;} public void setName(String n){name=n;} public Sport getSport(){return sport;} public void setSport(Sport s){sport=s;} }
