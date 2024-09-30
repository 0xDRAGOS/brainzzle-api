package com.brainzzle.brainzzle_api.controllers;

import com.brainzzle.brainzzle_api.dto.response.QuizDTO;
import com.brainzzle.brainzzle_api.services.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/create")
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody QuizDTO quizDTO) {
        QuizDTO createdQuiz = quizService.save(quizDTO);

        URI location = URI.create("/quiz/" + createdQuiz.getId());
        return ResponseEntity.created(location).body(createdQuiz);
    }

    @GetMapping
    public ResponseEntity<List<QuizDTO>> getAllQuizzes() {
        List<QuizDTO> quizzes = quizService.getAll();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDTO> getQuiz(@PathVariable("quizId") Long quizId) {
        QuizDTO quiz = quizService.getById(quizId);
        return ResponseEntity.ok(quiz);
    }

    @PutMapping("/update/{quizId}")
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable("quizId") Long quizId, @RequestBody QuizDTO quizDTO) {
        QuizDTO updatedQuiz = quizService.update(quizId, quizDTO);
        return ResponseEntity.ok(updatedQuiz);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.delete(quizId);
        return ResponseEntity.noContent().build();
    }
}
