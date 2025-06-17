package com.codemap.core.jobposting.domain;

import com.codemap.core.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;       // 공고 제목
    private String site;        // ex: 원티드, 사람인 등
    private String url;         // 채용 링크
    private LocalDate dueDate;  // 마감일
    private LocalDate lastCheckedDate; // 사용자가 마지막으로 채용공고를 확인한 날짜

    private boolean isTarget;   // 목표 공고 여부
    private boolean isDeleted;  // soft delete 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}