package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.ExtraMuralType;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface ExtraMuralTypeRepository extends JpaRepository<ExtraMuralType, Long> { Page<ExtraMuralType> findBySchoolId(Long schoolId, Pageable pageable); }
