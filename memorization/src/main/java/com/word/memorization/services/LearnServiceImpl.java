package com.word.memorization.services;


import com.word.memorization.entities.KnowledgeLevel;
import com.word.memorization.entities.Word;
import com.word.memorization.exceptions.UnsupportedValueException;
import com.word.memorization.exceptions.WordDoesNotExistsException;
import com.word.memorization.repositories.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class LearnServiceImpl implements LearnService{

    @Autowired
    private WordRepository wordRepository;


    /**
     *
     * @param claims jwt payload
     *
     * @return word that user should repeat
     *
     */
    @Override
    public Word getWord(Map<String, Object> claims) {
        Word word = wordRepository.findNextWordForUser((Long) claims.get("id"))
                .orElseThrow(()-> new WordDoesNotExistsException());

        word.setLastSeen(new Date());
        wordRepository.save(word);
        return word;
    }


    /**
     *
     * @param pageNumber page number for pagination
     * @param level level value for word`s knowledge
     * @param claims jwt payload
     *
     * @return list of words that sorted by knowledge level
     *
     */
    @Override
    public List<Word> getWordsByKnowledgeLevel(int pageNumber, int level, Map<String, Object> claims) {
        Pageable page = PageRequest.of(pageNumber - 1, 10);
        Page<Word> words = wordRepository.findAllByUserIdAndKnowledgeLevel((Long) claims.get("id"), level, page);
        return words.getContent();
    }

    /**
     *
     * @param level level value for word`s knowledge
     * @param word word that we need update
     * @param claims jwt payload
     *
     * @return word to update knowledge level
     *
     */
    @Override
    public Word updateWordKnowledgeLevel(int level, Word word, Map<String, Object> claims) {
        Word w = wordRepository.findByUserIdAndWord((Long) claims.get("id"), word.getWord())
                .orElseThrow(()-> new WordDoesNotExistsException());

        if(level <= KnowledgeLevel.EXCELLENT.getValue() && level >= KnowledgeLevel.NONE.getValue()) {
            w.setKnowledgeLevel(level);
        } else {
            throw new UnsupportedValueException();
        }

        wordRepository.save(w);
        return  w;
    }
}
