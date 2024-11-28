package com.word.memorization.services;

import com.word.memorization.dtos.WordDto;
import com.word.memorization.entities.KnowledgeLevel;
import com.word.memorization.entities.Word;
import com.word.memorization.exceptions.WordAlreadyExistsException;
import com.word.memorization.exceptions.WordDoesNotExistsException;
import com.word.memorization.repositories.WordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class CrudServiceImpl implements CrudService{
    @Autowired
    private WordRepository wordRepository;

    /**
     *
     * @param wordDto dto для слова
     * @param userId user id
     *
     * @return добавленое слово
     */
    @Override
    public Word addWord(WordDto wordDto, Long userId){
            if(wordRepository.findByUserIdAndWord(userId, wordDto.getWord()).isPresent()){
                throw new WordAlreadyExistsException();
            }
            Word word = Word.builder()
                    .userId(userId)
                    .word(wordDto.getWord())
                    .languageCode(wordDto.getLanguageCode())
                    .transcription(wordDto.getTranscription())
                    .translation(wordDto.getTranslation())
                    .knowledgeLevel(KnowledgeLevel.NONE.getValue())
                    .notes(wordDto.getNotes())
                    .createdAt(new Date())
                    .lastSeen(new Date())
                    .build();

            wordRepository.save(word);
            return word;
    }

    /**
     *
     * @param word слово
     * @param userId данные пользователя
     *
     * Удаляет слово
     */
    @Override
    public void deleteWord(String word, Long userId) {
        Optional<Word> w = wordRepository.findByUserIdAndWord(userId,word);
        if(w.isEmpty()) throw new WordDoesNotExistsException();
        wordRepository.delete(w.get());
    }

    /**
     *
     * @param pageNumber Номер страницы
     *
     * @return Список слов
     */
    @Override
    public List<Word> getWords(int pageNumber, Long userId) {
        Pageable page = PageRequest.of(pageNumber - 1, 10);
        Page<Word> words = wordRepository.findAllByUserIdOrderByCreatedAtDesc(userId, page);
        return words.getContent();
    }

    /**
     *
     * @param wordDto слово
     * @param userId данные пользователя
     *
     * @return Обновленное слово
     */
    @Override
    public Word updateWord(WordDto wordDto, Long userId) {
        Word word = wordRepository.findByUserIdAndWord(userId, wordDto.getWord())
                .orElseThrow(()-> new WordDoesNotExistsException());
        word.setTranscription(wordDto.getTranscription());
        word.setTranslation(wordDto.getTranslation());
        word.setNotes(wordDto.getNotes());

        wordRepository.save(word);
        return word;
    }
}
