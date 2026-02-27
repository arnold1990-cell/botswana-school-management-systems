package com.bosams.repository;
import com.bosams.domain.ExamSchedule;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> { List<ExamSchedule> findByExamGroupId(Long examGroupId); List<ExamSchedule> findByStreamId(Long streamId); }
