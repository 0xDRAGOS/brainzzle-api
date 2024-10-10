package com.brainzzle.brainzzle_api.dto.response;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class QuestionAnswerDTO {
    @NotBlank(message = "Answer text cannot be empty or null")
    private String answerText;

    @NotNull(message = "isCorrect cannot be null")
    private Boolean isCorrect;
}
