package com.brainzzle.brainzzle_api.repositories;

import com.brainzzle.brainzzle_api.entities.Question;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    Optional<Question> findById(long id);
    void deleteAllByQuizId(Long quizId);
}
