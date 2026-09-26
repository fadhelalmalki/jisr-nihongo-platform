package org.fadhel.jisrnihongoplatform.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "User ID is required")
    @Column(nullable = false)
    private Integer userId;

    @NotNull(message = "Course ID is required")
    @Column(nullable = false)
    private Integer courseId;

    @NotEmpty(message = "Status cannot be empty")
    @Pattern(regexp = "^(ACTIVE|COMPLETED|CANCELLED)$", message = "Status must be ACTIVE, COMPLETED, or CANCELLED")
    @Column(nullable = false)
    private String status;

    @NotNull(message = "Progress is required")
    @Min(0)
    @Max(100)
    @Column(nullable = false)
    private Integer progress;

    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
}
