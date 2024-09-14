package com.brainzzle.brainzzle_api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "images_questions")
public class QuestionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "image_url")
    private String imageUrl;

    public Long getImageId() {
        return imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
