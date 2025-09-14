package com.example.account;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
public class UserCreatedListener {
  private final AccountRepository repo;
  private final ObjectMapper om = new ObjectMapper();

  public UserCreatedListener(AccountRepository repo) { this.repo = repo; }

  @KafkaListener(topics = "user.created", groupId = "account-service-group")
  public void onUserCreated(String payload) throws Exception {
    Map m = om.readValue(payload, Map.class);
    String id = (String) m.get("id");
    // create an account with initial balance
    Account a = new Account();
    a.setId(UUID.fromString((String) id));
    a.setUserId(UUID.fromString((String) id));
    a.setBalance(new BigDecimal("100.00"));
    repo.save(a);
  }
}
