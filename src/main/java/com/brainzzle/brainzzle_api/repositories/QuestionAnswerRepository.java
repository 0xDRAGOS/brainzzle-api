package com.brainzzle.brainzzle_api.repositories;

import com.brainzzle.brainzzle_api.entities.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
}
