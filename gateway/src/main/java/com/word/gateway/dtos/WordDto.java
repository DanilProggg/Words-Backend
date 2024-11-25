package com.word.gateway.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "WordDto")
@ToString
public class WordDto {

    @Schema(description = "Word", example = "hello")
    @Size(max = 100, message = "The Word must contain up to 100 characters")
    @NotBlank(message = "The Word cant be null")
    private String word;

    @Schema(description = "Translation", example = "привет, здравствуйте")
    @Size(max = 200, message = "The Translation must contain up to 200 characters")
    @NotBlank(message = "The Translation cant be null")
    private String translation;

    @Schema(description = "Transcription", example = "хэлоу")
    @Size(max = 200, message = "The Transcription must contain up to 200 characters")
    private String transcription;

    @Schema(description = "Language Code", example = "ru")
    @Size(max = 10, message = "The Language Code must contain up to 10 characters")
    @NotBlank(message = "The Language Code cant be null")
    private String languageCode;

    @Schema(description = "Additional info. Examples", example = "Приветствие. Например: \"Привет, как дела?\"")
    @Size(max = 400, message = "The Additional info must contain up to 400 characters")
    private String notes;

}
