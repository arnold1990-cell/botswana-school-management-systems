package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.School;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SchoolRepository extends JpaRepository<School, Long> {}
