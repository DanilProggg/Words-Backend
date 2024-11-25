package com.word.memorization.controllers;

import com.word.memorization.dtos.JsonResponse;
import com.word.memorization.dtos.WordDto;
import com.word.memorization.entities.Word;
import com.word.memorization.exceptions.WordAlreadyExistsException;
import com.word.memorization.exceptions.WordDoesNotExistsException;
import com.word.memorization.services.CrudService;
import com.word.memorization.services.LearnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/crud")
@Slf4j
@Tag(name = "Crud")
public class CrudController {

    @Autowired
    private CrudService crudService;

    @Autowired
    private LearnService learnService;


    /*@PostMapping("/add")
    @Operation(summary = "Add word")
    public ResponseEntity<?> addWord(@RequestBody @Valid WordDto wordDto){
        try{

            crudService.addWord(wordDto, jwtTokenProvider.getClaims());
            return ResponseEntity.ok(new JsonResponse("The Word was added successful"));

        } catch (WordAlreadyExistsException e) {

            log.error(String.format("Word \"%s\" already exists. Error: %s",
                            wordDto.getWord(), e.getMessage()));

            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    String.format("Word \"%s\" already exists. Error: %s",
                            wordDto.getWord(), e.getMessage())
            );

        } catch (Exception e){

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    String.format("An unexpected error occurred. Error: %s",
                            e.getMessage())
            );

        }
    }
    @GetMapping("/get/all/{page}")
    @Operation(description = "Get page with user`s words")
    public ResponseEntity<?> getUsersWords(@PathVariable int page){
        try {
            List<Word> list = crudService.getWords(page, jwtTokenProvider.getClaims());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error(String.format("An unexpected error occurred. Error: %s",
                            e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/update")
    @Operation(description = "Update word")
    public ResponseEntity<?> updateWord(@RequestBody @Valid WordDto wordDto){
        try {
            crudService.updateWord(wordDto, jwtTokenProvider.getClaims());
            return ResponseEntity.ok(new JsonResponse("The word was updated successfully"));

        } catch (WordDoesNotExistsException e) {
            log.error(String.format("Word \"%s\"does not exist. Error: %s",
                    wordDto.getWord(), e.getMessage()));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e) {
            log.error(String.format("An unexpected error occurred. Error: %s",
                            e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{word}")
    @Operation(description = "Delete word")
    public ResponseEntity<?> deleteWord(@PathVariable String word){
        try {
            crudService.deleteWord(word, jwtTokenProvider.getClaims());
            return ResponseEntity.ok(new JsonResponse("The word was deleted successfully"));

        } catch (WordDoesNotExistsException e) {
            log.error(String.format("Word \"%s\"does not exist. Error: %s",
                    word, e.getMessage()));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e) {
            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }*/
}
