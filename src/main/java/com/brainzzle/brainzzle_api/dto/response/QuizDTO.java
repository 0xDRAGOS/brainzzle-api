package com.brainzzle.brainzzle_api.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private List<QuestionDTO> questions;
}
