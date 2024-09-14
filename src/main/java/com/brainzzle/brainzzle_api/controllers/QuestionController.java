package com.brainzzle.brainzzle_api.controllers;

import com.brainzzle.brainzzle_api.entities.Question;
import com.brainzzle.brainzzle_api.repositories.QuestionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionRepository repository;

    QuestionController(QuestionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Question getQuestion(@PathVariable("id") long questionId) {
        Optional<Question> question = repository.findById(questionId);
        return question.orElse(null);
    }
}
