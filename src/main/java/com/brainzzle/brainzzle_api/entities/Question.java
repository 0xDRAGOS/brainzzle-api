package com.brainzzle.brainzzle_api.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "question_id")
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "question_text")
    private String text;
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionAnswer> answers;
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionImage> images;
    protected Question() {}
    public Question(String text){
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public List<QuestionAnswer> getAnswers() {
        return answers;
    }

    public List<QuestionImage> getImages() {
        return images;
    }

    @Override
    public String toString() {
        return "Question{" +
                "text='" + text + '\'' +
                '}';
    }
}
