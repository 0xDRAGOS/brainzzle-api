package com.brainzzle.brainzzle_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizDetailDTO {
    private Long id;
    private String title;
    private String description;
    private List<QuestionDTO> questions;

    @Data
    public static class QuestionDTO {
        private Long questionId;
        private String questionText;
        private List<AnswerDTO> answers;

        @Data
        public static class AnswerDTO {
            private Long answerId;
            private String answerText;
        }
    }
}
