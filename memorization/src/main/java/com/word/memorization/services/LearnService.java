package com.word.memorization.services;

import com.word.memorization.entities.Word;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LearnService {
    Word getWord(Long userId);

    List<Word> getWordsByKnowledgeLevel(int pageNumber, int level, Long userId);

    Word updateWordKnowledgeLevel(int level, Word word,  Long userId);
}
