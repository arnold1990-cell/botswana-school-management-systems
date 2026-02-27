package com.bosams.repository;
import com.bosams.domain.GradeProfile;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface GradeProfileRepository extends JpaRepository<GradeProfile, Long> { Optional<GradeProfile> findByIsDefaultTrue(); }
