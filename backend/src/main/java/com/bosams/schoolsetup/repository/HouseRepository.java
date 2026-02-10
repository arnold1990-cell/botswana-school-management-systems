package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.House;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface HouseRepository extends JpaRepository<House, Long> { Page<House> findBySchoolId(Long schoolId, Pageable pageable); }
