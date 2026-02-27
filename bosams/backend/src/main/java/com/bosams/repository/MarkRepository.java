package com.bosams.repository;
import com.bosams.domain.MarkEntity;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface MarkRepository extends JpaRepository<MarkEntity, Long> { List<MarkEntity> findByExamScheduleId(Long scheduleId); Optional<MarkEntity> findByExamScheduleIdAndStudentId(Long scheduleId, Long studentId); }
