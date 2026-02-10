package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.House;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository extends JpaRepository<House, Long> {
    Page<House> findBySchoolId(Long schoolId, Pageable pageable);

    boolean existsBySchoolIdAndName(Long schoolId, String name);

    boolean existsBySchoolIdAndNameAndIdNot(Long schoolId, String name, Long id);
}
