package org.fadhel.jisrnihongoplatform.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Course ID is required")
    @Column(nullable = false)
    private Integer courseId;

    @NotEmpty(message = "Title cannot be empty")
    @Column(nullable = false)
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    @Column(nullable = false)
    private String description;

    @NotEmpty(message = "Lesson type cannot be empty")
    @Pattern(regexp = "^(Grammar|Vocabulary|Kanji|Listening|Reading)$", message = "Type must be Grammar, Vocabulary, Kanji, Listening, or Reading")
    @Column(nullable = false)
    private String type;

    @NotEmpty(message = "Video URL cannot be empty")
    @Column(nullable = false)
    private String videoUrl;

    @NotNull(message = "Lesson order is required")
    @Min(value = 1, message = "Lesson order must be at least 1")
    @Column(nullable = false)
    private Integer lessonOrder;
}
