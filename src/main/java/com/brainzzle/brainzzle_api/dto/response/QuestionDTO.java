package com.brainzzle.brainzzle_api.dto.response;

import com.brainzzle.brainzzle_api.entities.QuestionImage;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionDTO {
    @NotBlank(message = "Question text cannot be empty or null")
    private String questionText;
    private List<QuestionAnswerDTO> answers;
    private List<QuestionImage> images;
}
