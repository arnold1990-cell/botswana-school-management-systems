package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.Hostel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostelRepository extends JpaRepository<Hostel, Long> {
    Page<Hostel> findBySchoolId(Long schoolId, Pageable pageable);
}
