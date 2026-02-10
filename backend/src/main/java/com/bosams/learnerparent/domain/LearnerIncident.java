package com.bosams.learnerparent.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name="learner_incidents")
public class LearnerIncident extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private LocalDate incidentDate; @Enumerated(EnumType.STRING) private IncidentCategory category; @Column(columnDefinition="text") private String description; @Column(columnDefinition="text") private String actionTaken; private String reportedBy;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public LocalDate getIncidentDate(){return incidentDate;} public void setIncidentDate(LocalDate v){incidentDate=v;} public IncidentCategory getCategory(){return category;} public void setCategory(IncidentCategory v){category=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public String getActionTaken(){return actionTaken;} public void setActionTaken(String v){actionTaken=v;} public String getReportedBy(){return reportedBy;} public void setReportedBy(String v){reportedBy=v;}}
