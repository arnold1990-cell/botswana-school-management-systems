package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.Term;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface TermRepository extends JpaRepository<Term, Long> { Page<Term> findByAcademicYearId(Long academicYearId, Pageable pageable); }
