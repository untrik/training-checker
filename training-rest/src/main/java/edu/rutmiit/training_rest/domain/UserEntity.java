package edu.rutmiit.training_rest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private LocalDate birthDate;

    @Column
    private Integer weightKg;

    @Version
    private long version;

    protected UserEntity() {
    }

    public UserEntity(UUID id, String username, String email, LocalDate birthDate, Integer weightKg) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.birthDate = birthDate;
        this.weightKg = weightKg;
    }


}
