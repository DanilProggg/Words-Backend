package com.word.memorization.repositories;

import com.word.memorization.entities.Word;
import io.jsonwebtoken.security.Jwks;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    Optional<Word> findByWord(String word);

    Optional<Word> findByUserIdAndWord(Long userId, String word);

    //Using by LearnService
    Page<Word> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query(value = "select * from ( " +
                "select * from word " +
                "where user_id = ?1 " +
                "order by last_seen asc " +
            ") " +
            "where knowledge_level = 0 " +
            "or (knowledge_level = 1 and EXTRACT(EPOCH FROM (NOW() - last_seen)) < 180) " +
            "or (knowledge_level = 2 and EXTRACT(EPOCH FROM (NOW() - last_seen)) < 3600) " +
            "or (knowledge_level = 3 and NOW() - last_seen < INTERVAL '1 day') " +
            "limit 1", nativeQuery = true)
    Optional<Word> findNextWordForUser(Long userId);

    Page<Word> findAllByUserIdAndKnowledgeLevel(Long id, int level, Pageable pageable);

}
