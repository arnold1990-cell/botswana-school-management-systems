package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "grades", uniqueConstraints = @UniqueConstraint(columnNames = {"school_id","name"}))
public class Grade extends SchoolOwnedEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false,length=80) private String name;
    public Long getId(){return id;} public String getName(){return name;} public void setName(String n){name=n;}
}
