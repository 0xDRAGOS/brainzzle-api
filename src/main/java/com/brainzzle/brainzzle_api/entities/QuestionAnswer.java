package com.brainzzle.brainzzle_api.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "answers_questions")
@Data
public class QuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "answer_id")
    private Long answerId;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "answer_text")
    private String answerText;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    public Long getAnswerId() {
        return answerId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
