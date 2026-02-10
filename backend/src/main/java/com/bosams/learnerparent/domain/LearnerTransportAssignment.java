package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="learner_transport_assignments", uniqueConstraints=@UniqueConstraint(name="uq_transport_assign", columnNames={"school_id","learner_id","start_date"}))
public class LearnerTransportAssignment extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private Long busRouteId; private Long ticketTypeId; private LocalDate startDate; private LocalDate endDate;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public Long getBusRouteId(){return busRouteId;} public void setBusRouteId(Long v){busRouteId=v;} public Long getTicketTypeId(){return ticketTypeId;} public void setTicketTypeId(Long v){ticketTypeId=v;} public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}}
