package com.bosams.learnerparent.domain;

import com.bosams.common.AuditableEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="learner_absence_notifications")
public class LearnerAbsenceNotification extends SchoolScopedEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long learnerId; private LocalDate date; @Enumerated(EnumType.STRING) private NotificationChannel channel; @Column(columnDefinition="text") private String message; private Instant sentAt; @Enumerated(EnumType.STRING) private NotificationStatus status;
public Long getId(){return id;} public Long getLearnerId(){return learnerId;} public void setLearnerId(Long v){learnerId=v;} public LocalDate getDate(){return date;} public void setDate(LocalDate v){date=v;} public NotificationChannel getChannel(){return channel;} public void setChannel(NotificationChannel v){channel=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;} public Instant getSentAt(){return sentAt;} public void setSentAt(Instant v){sentAt=v;} public NotificationStatus getStatus(){return status;} public void setStatus(NotificationStatus v){status=v;}}
