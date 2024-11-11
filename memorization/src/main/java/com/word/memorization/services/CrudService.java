package com.word.memorization.services;

import com.word.memorization.dtos.WordDto;
import com.word.memorization.entities.Word;

import java.util.List;
import java.util.Map;

public interface CrudService {
    Word addWord(WordDto wordDto, Map<String,Object> claims);
    void deleteWord(String word, Map<String,Object> claims);
    List<Word> getWords(int pageNumber, Map<String, Object> claims);
    Word updateWord(WordDto wordDto, Map<String, Object> claims);

}
