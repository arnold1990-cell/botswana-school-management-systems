package com.bosams.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "teacher_profile")
@Getter @Setter @NoArgsConstructor
public class TeacherProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(optional = false) @JoinColumn(name = "user_id", unique = true) private UserEntity user;
    @Column(name = "staff_no", unique = true) private String staffNo;
    private String phone;
}
