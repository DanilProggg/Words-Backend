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

@Service
public class LearnServiceImpl implements LearnService{

    @Autowired
    private WordRepository wordRepository;


    /**
     *
     * @param userId user id
     *
     * @return word that user should repeat
     *
     */
    @Override
    public Word getWord(Long userId) {
        Word word = wordRepository.findNextWordForUser(userId)
                .orElseThrow(()-> new WordDoesNotExistsException());

        word.setLastSeen(new Date());
        wordRepository.save(word);
        return word;
    }


    /**
     *
     * @param pageNumber page number for pagination
     * @param level level value for word`s knowledge
     * @param userId jwt payload
     *
     * @return list of words that sorted by knowledge level
     *
     */
    @Override
    public List<Word> getWordsByKnowledgeLevel(int pageNumber, int level,  Long userId) {
        Pageable page = PageRequest.of(pageNumber - 1, 10);
        Page<Word> words = wordRepository.findAllByUserIdAndKnowledgeLevel(userId, level, page);
        return words.getContent();
    }

    /**
     *
     * @param level level value for word`s knowledge
     * @param word word that we need update
     * @param userId user id
     *
     * @return word to update knowledge level
     *
     */
    @Override
    public Word updateWordKnowledgeLevel(int level, Word word,  Long userId) {
        Word w = wordRepository.findByUserIdAndWord(userId, word.getWord())
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
