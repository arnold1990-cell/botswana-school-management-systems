package com.bosams.schoolsetup.domain;

import jakarta.persistence.*;
@Entity @Table(name="demerit_codes", uniqueConstraints=@UniqueConstraint(columnNames={"school_id","code"}))
public class DemeritCode extends SchoolOwnedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,length=40) private String code; @Column(nullable=false,length=120) private String description; public Long getId(){return id;} public String getCode(){return code;} public void setCode(String c){code=c;} public String getDescription(){return description;} public void setDescription(String d){description=d;} }
