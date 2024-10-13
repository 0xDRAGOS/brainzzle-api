package com.brainzzle.brainzzle_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizPageDTO {
    private List<QuizSummaryDTO> content;
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;

    public QuizPageDTO(Page<QuizSummaryDTO> page) {
        this.content = page.getContent();
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
