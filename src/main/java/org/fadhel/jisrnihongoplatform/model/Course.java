package org.fadhel.jisrnihongoplatform.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Instructor ID is required")
    @Column(nullable = false)
    private Integer instructorId;

    @NotEmpty(message = "Title cannot be empty")
    @Column(nullable = false)
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotEmpty(message = "Level cannot be empty")
    @Pattern(regexp = "^(N5|N4|N3|N2|N1)$", message = "Level must be N5, N4, N3, N2, or N1")
    @Column(nullable = false)
    private String level;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be zero or positive")
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull(message = "Duration hours is required")
    @Min(value = 1, message = "Duration must be at least 1 hour")
    @Column(nullable = false)
    private Integer durationHours;

}
