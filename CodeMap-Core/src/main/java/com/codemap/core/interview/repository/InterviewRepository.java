package com.codemap.core.interview.repository;

import com.codemap.core.interview.domain.InterviewQuestion;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterviewRepository extends JpaRepository<InterviewQuestion,Long> {

    List<InterviewQuestion> findByCategory(String category);

    @Query("SELECT DISTINCT iq.category FROM InterviewQuestion iq")
    List<String> findDistinctCategory();

    @Query(value = "SELECT iq FROM InterviewQuestion iq WHERE iq.category IN :categories ORDER BY FUNCTION('RAND')")
    List<InterviewQuestion> findRandomFiveByCategoryIn(@Param("categories") List<String> categories, Pageable pageable);
}