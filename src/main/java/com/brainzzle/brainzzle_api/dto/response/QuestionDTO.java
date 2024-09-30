package com.brainzzle.brainzzle_api.dto.response;

import com.brainzzle.brainzzle_api.entities.QuestionImage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionDTO {
    private String text;
    private List<QuestionAnswerDTO> answers;
    private List<QuestionImage> images;
}
