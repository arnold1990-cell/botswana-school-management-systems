package com.bosams.service;

import com.bosams.domain.Enums;
import com.bosams.domain.SubjectEntity;
import com.bosams.repository.SubjectRepository;
import com.bosams.common.ApiException;
import com.bosams.web.dto.SubjectDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectsService {
    private final SubjectRepository subjects;

    public SubjectsService(SubjectRepository subjects) {
        this.subjects = subjects;
    }

    public List<SubjectDto.SubjectResponse> listSubjects(Enums.SchoolLevel level, Integer grade) {
        if (grade != null && grade < 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_GRADE", "grade must be at least 1");
        }

        List<SubjectEntity> found;

        if (level != null && grade != null) {
            found = subjects.findBySchoolLevelAndStatusAndGradeFromLessThanEqualAndGradeToGreaterThanEqualOrderByGradeFromAscNameAsc(
                    level,
                    Enums.EntityStatus.ACTIVE,
                    grade,
                    grade
            );
        } else if (level != null) {
            found = subjects.findBySchoolLevelAndStatusOrderByGradeFromAscNameAsc(level, Enums.EntityStatus.ACTIVE);
        } else if (grade != null) {
            found = subjects.findByStatusAndGradeFromLessThanEqualAndGradeToGreaterThanEqualOrderBySchoolLevelAscGradeFromAscNameAsc(
                    Enums.EntityStatus.ACTIVE,
                    grade,
                    grade
            );
        } else {
            found = subjects.findByStatusOrderBySchoolLevelAscGradeFromAscNameAsc(Enums.EntityStatus.ACTIVE);
        }

        return found.stream().map(SubjectDto.SubjectResponse::fromEntity).toList();
    }
}
