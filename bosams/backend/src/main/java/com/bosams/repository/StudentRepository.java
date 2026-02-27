package com.bosams.repository;
import com.bosams.domain.StudentEntity;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface StudentRepository extends JpaRepository<StudentEntity, Long> { List<StudentEntity> findByStreamId(Long streamId); }
