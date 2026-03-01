package com.bosams.repository;

import com.bosams.domain.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    List<Term> findByAcademicYearYearOrderByTermNo(Integer year);
    Optional<Term> findByAcademicYearYearAndTermNo(Integer year, Integer termNo);
}
