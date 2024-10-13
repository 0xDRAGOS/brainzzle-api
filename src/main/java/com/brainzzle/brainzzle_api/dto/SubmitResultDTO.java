package com.brainzzle.brainzzle_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitResultDTO {
    private int score;
    private int totalQuestions;
}
