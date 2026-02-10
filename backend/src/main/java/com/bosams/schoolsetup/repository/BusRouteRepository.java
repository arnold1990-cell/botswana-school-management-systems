package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.BusRoute;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface BusRouteRepository extends JpaRepository<BusRoute, Long> { Page<BusRoute> findBySchoolId(Long schoolId, Pageable pageable); }
