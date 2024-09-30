package com.brainzzle.brainzzle_api.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionAnswerDTO {
    private String text;
    private Boolean isCorrect;
}
