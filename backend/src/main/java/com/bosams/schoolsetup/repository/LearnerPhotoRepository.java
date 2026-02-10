package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.LearnerPhoto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerPhotoRepository extends JpaRepository<LearnerPhoto, UUID> {
    Page<LearnerPhoto> findByLearnerId(UUID learnerId, Pageable pageable);
}
