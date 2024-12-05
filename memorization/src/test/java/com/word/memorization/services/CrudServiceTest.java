package com.word.memorization.services;

import com.word.memorization.entities.Word;
import com.word.memorization.repositories.WordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrudServiceTest {
    @InjectMocks
    private CrudServiceImpl crudService;
    @Mock
    private WordRepository wordRepository;
    @Test
    void testGetWords() {
        int pageNumber = 1;
        Long userId = 1L;
        Pageable pageable = PageRequest.of(pageNumber - 1, 10);

        Word word1 = Word.builder()
                .id(1L)
                .userId(1L)
                .word("Hello")
                .languageCode("en")
                .transcription("[həˈləʊ]")
                .translation("Привет")
                .knowledgeLevel(0)
                .notes("Common greeting")
                .lastSeen(new Date())
                .createdAt(new Date())
                .build();

        Word word2 = Word.builder()
                .id(2L)
                .userId(1L)
                .word("World")
                .languageCode("en")
                .transcription("[wɜːld]")
                .translation("Мир")
                .knowledgeLevel(2)
                .notes("Common noun")
                .lastSeen(new Date())
                .createdAt(new Date())
                .build();

        List<Word> mockWords = List.of(word1, word2);
        Page<Word> mockPage = new PageImpl<>(mockWords, pageable, mockWords.size());
        when(wordRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)).thenReturn(mockPage);

        // Act: Вызов тестируемого метода
        List<Word> result = crudService.getWords(pageNumber, userId);

        // Assert: Проверка результата
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Hello", result.get(0).getWord());
        assertEquals("en", result.get(0).getLanguageCode());
        assertEquals("[həˈləʊ]", result.get(0).getTranscription());
        assertEquals("Привет", result.get(0).getTranslation());
        assertEquals(0, result.get(0).getKnowledgeLevel());
        assertEquals("Common greeting", result.get(0).getNotes());
        assertEquals(Date.class, result.get(0).getLastSeen().getClass());
        assertEquals(Date.class, result.get(0).getCreatedAt().getClass());

        assertEquals("World", result.get(1).getWord());
        assertEquals("en", result.get(1).getLanguageCode());
        assertEquals("[wɜːld]", result.get(1).getTranscription());
        assertEquals("Мир", result.get(1).getTranslation());
        assertEquals(2, result.get(1).getKnowledgeLevel());
        assertEquals("Common noun", result.get(1).getNotes());
        assertEquals(Date.class, result.get(1).getLastSeen().getClass());
        assertEquals(Date.class, result.get(1).getCreatedAt().getClass());

        // Проверить, что репозиторий был вызван с нужными параметрами
        verify(wordRepository, times(1)).findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
