package com.brainzzle.brainzzle_api.repositories;

import com.brainzzle.brainzzle_api.entities.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByIdAndUserId(Long id, Long userId);
    List<Quiz> findAllByUserId(Long userId);
    Page<Quiz> findAllByUserId(Long userId, Pageable pageable);
    Page<Quiz> findAllByIsPublic(Boolean isPublic, Pageable pageable);

    @Modifying
    @Query("UPDATE Quiz q SET q.submitCount = q.submitCount + 1 WHERE q.id = :quizId")
    void incrementSubmitCount(@Param("quizId") Long quizId);

    @Query(value = "SELECT * FROM quizzes WHERE is_public = true ORDER BY submit_count DESC LIMIT :topNumber", nativeQuery = true)
    List<Quiz> findTopPublicQuizzes(@Param("topNumber") int topNumber);
}
