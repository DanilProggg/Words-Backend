package com.word.memorization.services;

import com.word.memorization.dtos.WordDto;
import com.word.memorization.entities.Word;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface LearnService {
    Word getWord(Map<String,Object> claims);

    List<Word> getWordsByKnowledgeLevel(int pageNumber, int level, Map<String, Object> claims);

    Word updateWordKnowledgeLevel(int level, Word word, Map<String, Object> claims);
}
