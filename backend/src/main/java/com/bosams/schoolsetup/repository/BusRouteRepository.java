package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.BusRoute;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRouteRepository extends JpaRepository<BusRoute, UUID> {
}
