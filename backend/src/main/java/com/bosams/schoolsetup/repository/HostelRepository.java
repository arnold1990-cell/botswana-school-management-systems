package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.Hostel;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostelRepository extends JpaRepository<Hostel, UUID> {
}
