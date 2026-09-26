package org.fadhel.jisrnihongoplatform.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Enrollment ID is required")
    @Column(nullable = false, unique = true)
    private Integer enrollmentId;

    @NotEmpty(message = "Certificate number cannot be empty")
    @Column(nullable = false, unique = true)
    private String certificateNumber;

    private LocalDateTime issuedAt;
}
