package com.bosams.web.dto;

import com.bosams.domain.Enums;
import com.bosams.domain.SubjectEntity;

public final class SubjectDto {
    private SubjectDto() {
    }

    public record SubjectResponse(
            Long id,
            String name,
            String code,
            Enums.SchoolLevel level,
            Integer gradeFrom,
            Integer gradeTo,
            boolean elective
    ) {
        public static SubjectResponse fromEntity(SubjectEntity subject) {
            return new SubjectResponse(
                    subject.getId(),
                    subject.getName(),
                    subject.getCode(),
                    subject.getSchoolLevel(),
                    subject.getGradeFrom(),
                    subject.getGradeTo(),
                    subject.isElective()
            );
        }
    }
}
