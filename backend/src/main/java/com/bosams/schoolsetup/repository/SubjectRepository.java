package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.Subject;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {
}
