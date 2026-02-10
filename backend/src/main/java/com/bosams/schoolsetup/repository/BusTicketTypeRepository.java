package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.BusTicketType;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface BusTicketTypeRepository extends JpaRepository<BusTicketType, Long> { Page<BusTicketType> findBySchoolId(Long schoolId, Pageable pageable); }
