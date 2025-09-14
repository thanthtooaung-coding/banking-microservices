package com.example.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {
  @Id
  private UUID id;

  @Column(name="user_id", nullable=false)
  private UUID userId;

  @Column(nullable=false)
  private BigDecimal balance;

  @Column(name="created_at")
  private Instant createdAt = Instant.now();

  public Account() { this.id = UUID.randomUUID(); }
  
  public UUID getId() { return id; }

    public UUID getUserId() { return userId; }

    public void setUserId(UUID userId) { this.userId = userId; }

    public BigDecimal getBalance() { return balance; }

    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public Instant getCreatedAt() { return createdAt; }

}
