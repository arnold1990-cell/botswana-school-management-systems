package com.bosams.domain;

public class Enums {
    public enum Role { ADMIN, PRINCIPAL, TEACHER, STUDENT, PARENT, ACCOUNTANT }
    public enum UserStatus { ACTIVE, INACTIVE }
    public enum Gender { MALE, FEMALE }
    public enum EntityStatus { ACTIVE, INACTIVE }
    public enum ExamStatus { ACTIVE, INACTIVE }
    public enum MarkResult { PASS, FAIL, ABSENT }
    public enum AssessmentType { CAT, EXAM }
    public enum GradeLetter { A, B, C, D, E, F }
    public enum MarkEntryStatus { DRAFT, SUBMITTED }
}
