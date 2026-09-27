package org.fadhel.jisrnihongoplatform.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Name cannot be empty")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(nullable = false)
    private String name;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @NotEmpty(message = "Japanese level cannot be empty")
    @Pattern(regexp = "^(N5|N4|N3|N2|N1|Beginner)$", message = "Japanese level must be N5, N4, N3, N2, N1, or Beginner")
    @Column(nullable = false)
    private String japaneseLevel;

    @NotEmpty(message = "Learning goal cannot be empty")
    @Column(nullable = false)
    private String learningGoal;


    // validated as E.164, which is the only format the WhatsApp gateway accepts
    @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "Phone must be in E.164 format, for example +966512345678")
    @Column(length = 16)
    private String phone;
}
