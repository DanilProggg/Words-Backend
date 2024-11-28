package com.word.memorization.services;

import com.word.memorization.dtos.WordDto;
import com.word.memorization.entities.Word;

import java.util.List;
import java.util.Map;

public interface CrudService {
    Word addWord(WordDto wordDto, Long userId);
    void deleteWord(WordDto wordDto, Long userId);
    List<Word> getWords(int pageNumber, Long userId);
    Word updateWord(WordDto wordDto, Long userId);

}
