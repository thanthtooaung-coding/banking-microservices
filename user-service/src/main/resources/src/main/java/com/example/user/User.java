package com.example.user;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    public User() { this.id = UUID.randomUUID(); }

    public UUID getId() { return id; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
}
