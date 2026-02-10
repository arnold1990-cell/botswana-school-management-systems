package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.SchoolProfile;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolProfileRepository extends JpaRepository<SchoolProfile, UUID> {
}
