package com.brainzzle.brainzzle_api.controllers;

import com.brainzzle.brainzzle_api.dto.QuizSummaryDTO;
import com.brainzzle.brainzzle_api.dto.SubmitQuizDTO;
import com.brainzzle.brainzzle_api.dto.SubmitResultDTO;
import com.brainzzle.brainzzle_api.dto.QuizDTO;
import com.brainzzle.brainzzle_api.dto.QuizDetailDTO;
import com.brainzzle.brainzzle_api.services.QuizService;
import org.springframework.data.domain.Page;
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

    @GetMapping("/summaries")
    public ResponseEntity<Page<QuizSummaryDTO>> getQuizSummaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<QuizSummaryDTO> quizSummaries = quizService.getQuizSummaries(page, size);
        return ResponseEntity.ok(quizSummaries);
    }


    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDTO> getQuiz(@PathVariable("quizId") Long quizId) {
        QuizDTO quiz = quizService.getById(quizId);
        return ResponseEntity.ok(quiz);
    }

    @GetMapping("/details/{quizId}")
    public ResponseEntity<QuizDetailDTO> getQuizDetail(@PathVariable("quizId") Long quizId) {
        QuizDetailDTO quizDetail = quizService.getQuizDetailById(quizId);
        return ResponseEntity.ok(quizDetail);
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

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<SubmitResultDTO> submitQuiz(@PathVariable Long quizId, @RequestBody SubmitQuizDTO submitQuizDTO) {
        SubmitResultDTO result = quizService.submitQuiz(quizId, submitQuizDTO);
        return ResponseEntity.ok(result);
    }
}
