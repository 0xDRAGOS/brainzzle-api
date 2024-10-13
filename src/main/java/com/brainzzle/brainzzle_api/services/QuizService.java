package com.brainzzle.brainzzle_api.services;

import com.brainzzle.brainzzle_api.dto.*;
import com.brainzzle.brainzzle_api.entities.*;
import com.brainzzle.brainzzle_api.exceptions.ResourceNotFoundException;
import com.brainzzle.brainzzle_api.exceptions.UnauthorizedException;
import com.brainzzle.brainzzle_api.repositories.QuestionRepository;
import com.brainzzle.brainzzle_api.repositories.QuizRepository;
import com.brainzzle.brainzzle_api.services.auth.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import org.modelmapper.TypeToken;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository, UserService userService, ModelMapper modelMapper) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public QuizDTO save(QuizDTO quizDTO) {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        Long userId = user.get().getId();
        Quiz newQuiz = modelMapper.map(quizDTO, Quiz.class);
        newQuiz.setUserId(userId);

        populateQuizRelations(userId, newQuiz);

        Quiz createdQuiz = quizRepository.save(newQuiz);

        return modelMapper.map(createdQuiz, QuizDTO.class);
    }

    @Transactional
    public QuizDTO update(Long quizId, QuizDTO quizDTO) throws UnauthorizedException {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        Long userId = user.get().getId();
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);

        if (quizOptional.isPresent()) {
            Quiz updatedQuiz = quizOptional.get();

            if (!Objects.equals(updatedQuiz.getUserId(), userId)) {
                throw new UnauthorizedException("User is not authorized to do this action.");
            }

            updatedQuiz.setTitle(quizDTO.getTitle());
            updatedQuiz.setDescription(quizDTO.getDescription());

            if (quizDTO.getQuestions().size() < updatedQuiz.getQuestions().size()) {
                questionRepository.deleteAllByQuizId(quizId);
                updatedQuiz.getQuestions().clear();
            }

            Type questionListType = new TypeToken<List<Question>>() {}.getType();
            List<Question> questionList = modelMapper.map(quizDTO.getQuestions(), questionListType);
            updatedQuiz.getQuestions().addAll(questionList);

            populateQuizRelations(userId, updatedQuiz);

            quizRepository.save(updatedQuiz);
            return modelMapper.map(updatedQuiz, QuizDTO.class);
        } else {
            throw new ResourceNotFoundException("Quiz not found.");
        }
    }


    public List<QuizDTO> getAll() {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        Long userId = user.get().getId();
        Type quizDTOType = new TypeToken<List<QuizDTO>>() {}.getType();
        List<QuizDTO> quizzes = modelMapper.map(quizRepository.findAllByUserId(userId), quizDTOType);

        if (quizzes.isEmpty()) {
            throw new ResourceNotFoundException("No quiz associated with this user has been found.");
        }

        return quizzes;
    }

    public QuizDTO getById(Long quizId) {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        Long userId = user.get().getId();
        Optional<Quiz> quizOptional = quizRepository.findByIdAndUserId(quizId, userId);
        if (quizOptional.isPresent()) {
            return modelMapper.map(quizOptional.get(), QuizDTO.class);
        } else {
            throw new ResourceNotFoundException("No quiz associated with this user has been found.");
        }
    }

    public QuizDetailDTO getQuizDetailById(Long quizId) {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        Long userId = user.get().getId();
        Optional<Quiz> quizOptional = quizRepository.findByIdAndUserId(quizId, userId);
        if (quizOptional.isPresent()) {
            return modelMapper.map(quizOptional.get(), QuizDetailDTO.class);
        } else {
            throw new ResourceNotFoundException("No quiz associated with this user has been found.");
        }
    }

    public Page<QuizSummaryDTO> getQuizSummaries(int page, int size) {
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        Long userId = user.get().getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Quiz> quizzes = quizRepository.findAllByUserId(userId, pageable);

        return quizzes.map(quiz -> modelMapper.map(quiz, QuizSummaryDTO.class));
    }

    @Transactional
    public void delete(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found.");
        }

        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        Long userId = user.get().getId();

        Optional<Quiz> quizOptional = quizRepository.findByIdAndUserId(quizId, userId);
        if (quizOptional.isPresent()) {
            quizRepository.delete(quizOptional.get());
        } else {
            throw new UnauthorizedException("User is not authorized to delete this quiz.");
        }
    }

    private void populateQuizRelations(Long userId, Quiz quiz) {
        if (quiz.getQuestions() != null) {
            quiz.getQuestions().forEach(question -> {
                question.setQuiz(quiz);
                question.setUserId(userId);

                if (question.getAnswers() != null) {
                    question.getAnswers().forEach(answer -> {
                        answer.setQuestion(question);
                        answer.setUserId(userId);
                    });
                }

                if (question.getImages() != null) {
                    question.getImages().forEach(image -> image.setQuestion(question));
                }
            });
        }
    }

    @Transactional
    public SubmitResultDTO submitQuiz(Long quizId, SubmitQuizDTO submitQuizDTO) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        int score = 0;
        int totalQuestions = quiz.getQuestions().size();

        for (SubmittedAnswerDTO submittedAnswer : submitQuizDTO.getAnswers()) {
            Question question = quiz.getQuestions().stream()
                    .filter(q -> q.getId().equals(submittedAnswer.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

            List<Long> correctAnswers = question.getAnswers().stream()
                    .filter(QuestionAnswer::getIsCorrect)
                    .map(QuestionAnswer::getAnswerId)
                    .toList();

            boolean isCorrect = new HashSet<>(submittedAnswer.getSelectedAnswerIds()).containsAll(correctAnswers) &&
                    new HashSet<>(correctAnswers).containsAll(submittedAnswer.getSelectedAnswerIds());

            if (isCorrect) {
                score++;
            }
        }

        SubmitResultDTO result = new SubmitResultDTO();
        result.setScore(score);
        result.setTotalQuestions(totalQuestions);

        return result;
    }
}
