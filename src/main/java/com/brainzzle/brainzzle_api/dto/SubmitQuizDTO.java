package com.brainzzle.brainzzle_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuizDTO {
    private List<SubmittedAnswerDTO> answers;
}
