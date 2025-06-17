package com.codemap.core.jobposting.dto;

import com.codemap.core.jobposting.domain.JobPosting;
import java.time.LocalDate;

public class JobPostingResponse {

    private Long id;
    private String title;
    private LocalDate dueDate;

    public JobPostingResponse(Long id, String title, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
    }

    public static JobPostingResponse from(JobPosting posting) {
        return new JobPostingResponse(
            posting.getId(),
            posting.getTitle(),
            posting.getDueDate()
        );
    }

    // getter들 추가
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public LocalDate getDueDate() { return dueDate; }
}