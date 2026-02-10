package com.bosams.learnerparent.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="parent_guardians", uniqueConstraints=@UniqueConstraint(name="uq_parent_no", columnNames={"school_id","parent_no"}))
public class ParentGuardian extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String parentNo; @Column(nullable=false) private String firstName; @Column(nullable=false) private String lastName; private String nationalId; private String email; private String phone; private String address; @Enumerated(EnumType.STRING) private ParentStatus status; private Instant archivedAt; private String archiveReason;
public Long getId(){return id;} public String getParentNo(){return parentNo;} public void setParentNo(String v){parentNo=v;} public String getFirstName(){return firstName;} public void setFirstName(String v){firstName=v;} public String getLastName(){return lastName;} public void setLastName(String v){lastName=v;} public String getNationalId(){return nationalId;} public void setNationalId(String v){nationalId=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getAddress(){return address;} public void setAddress(String v){address=v;} public ParentStatus getStatus(){return status;} public void setStatus(ParentStatus v){status=v;} public Instant getArchivedAt(){return archivedAt;} public void setArchivedAt(Instant v){archivedAt=v;} public String getArchiveReason(){return archiveReason;} public void setArchiveReason(String v){archiveReason=v;}
}
