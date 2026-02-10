package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.BusTicketType;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusTicketTypeRepository extends JpaRepository<BusTicketType, UUID> {
}
