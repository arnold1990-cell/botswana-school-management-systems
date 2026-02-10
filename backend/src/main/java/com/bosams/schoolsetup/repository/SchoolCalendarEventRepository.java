package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.SchoolCalendarEvent;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolCalendarEventRepository extends JpaRepository<SchoolCalendarEvent, UUID> {
    long countByTermId(UUID termId);
}
