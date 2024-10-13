package com.brainzzle.brainzzle_api.repositories;

import com.brainzzle.brainzzle_api.entities.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByIdAndUserId(Long id, Long userId);
    List<Quiz> findAllByUserId(Long userId);
    Page<Quiz> findAllByUserId(Long userId, Pageable pageable);
}
