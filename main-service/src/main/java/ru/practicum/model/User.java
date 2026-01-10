package ru.practicum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Length(min = 6, max = 254)
    @NotNull
    @Column(nullable = false, unique = true, name = "email")
    private String email;

    @Length(min = 2, max = 250)
    @NotBlank
    @Column(nullable = false, name = "name")
    private String name;
}
