package com.word.memorization.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_word_name", columnList = "word"),
                @Index(name = "idx_word_last_seen", columnList = "last_seen")
        }
)
public class Word {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_id_seq")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "language_code", nullable = false)
    private String languageCode;

    @Column(name = "transcription", nullable = true)
    private String transcription; //not necessary

    @Column(name = "translation", nullable = false)
    private String translation;

    @Column(name = "knowledge_level", nullable = false)
    private int knowledgeLevel; //it is issued automatically

    @Column(name = "notes", nullable = true)
    private String notes;

    @Column(name = "last_seen", nullable = false)
    private Date lastSeen;

    @Column(name = "create_at", nullable = false)
    private Date createdAt;
}

