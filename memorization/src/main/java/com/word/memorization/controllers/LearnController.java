package com.word.memorization.controllers;

import com.word.memorization.components.JwtTokenProvider;
import com.word.memorization.exceptions.WordAlreadyExistsException;
import com.word.memorization.exceptions.WordDoesNotExistsException;
import com.word.memorization.services.LearnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learn")
@Slf4j
@Tag(name = "Learning Controller")
public class LearnController {
    @Autowired
    private LearnService learnService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/get")
    @Operation(summary = "Get next word to learn")
    public ResponseEntity<?> learnWord(){
        try {
            return ResponseEntity.ok(learnService.getWord(jwtTokenProvider.getClaims()));

        } catch (WordDoesNotExistsException e) {

            log.error(String.format("Words to learn are ended. Error: %s",
                    e.getMessage()));

            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    String.format("Words to learn are ended. Error: %s",
                            e.getMessage())
            );

        } catch (Exception e) {

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    String.format("An unexpected error occurred. Error: %s",
                            e.getMessage()));
        }
    }

    @Operation(summary = "Get word`s list by knowledge")
    @GetMapping("/knowledgeLevel/{level}/{page}")
    public ResponseEntity<?> getWordsByKnowledgeLevel(@PathVariable int level,
                                                      @PathVariable int page){
        try {
            return ResponseEntity.ok(learnService.getWordsByKnowledgeLevel(page, level, jwtTokenProvider.getClaims()));

        } catch (Exception e) {

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    String.format("An unexpected error occurred. Error: %s",
                            e.getMessage()));
        }
    }
}
