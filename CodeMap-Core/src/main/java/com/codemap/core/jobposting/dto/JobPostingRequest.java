package com.codemap.core.jobposting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class JobPostingRequest {

    private String companyName;

    @JsonFormat(pattern = "yyyy-MM-dd") // Jackson용
    @DateTimeFormat(pattern = "yyyy-MM-dd") // Form 데이터용
    private LocalDate dueDate;

    public JobPostingRequest() {
    }

    public JobPostingRequest(String companyName, LocalDate dueDate) {
        this.companyName = companyName;
        this.dueDate = dueDate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}