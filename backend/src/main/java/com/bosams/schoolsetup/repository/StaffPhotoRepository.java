package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.StaffPhoto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffPhotoRepository extends JpaRepository<StaffPhoto, UUID> {
    Page<StaffPhoto> findByStaffId(UUID staffId, Pageable pageable);
}
