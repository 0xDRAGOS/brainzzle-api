package com.brainzzle.brainzzle_api.dto;


import com.brainzzle.brainzzle_api.entities.QuestionImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private List<QuestionDTO> questions;

    @Data
    public static class QuestionDTO {
        private Long questionId;
        @NotBlank(message = "Question text cannot be empty or null")
        private String questionText;
        private List<AnswerDTO> answers;
        private List<QuestionImage> images;

        @Data
        public static class AnswerDTO {
            private Long answerId;
            @NotBlank(message = "Answer text cannot be empty or null")
            private String answerText;

            @NotNull(message = "isCorrect cannot be null")
            private Boolean isCorrect;
        }

//        @Data
//        public static class QuestionImageDTO {
//            private Long imageId;
//            private String imageUrl;
//        }

    }
}
