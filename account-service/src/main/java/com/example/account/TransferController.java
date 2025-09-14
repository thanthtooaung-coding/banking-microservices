package com.example.account;

import com.example.proto.UserRequest;
import com.example.proto.UserResponse;
import com.example.proto.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class TransferController {
  private final AccountRepository repo;
  private final KafkaTemplate<String,String> kafka;
  private final JwtUtil jwtUtil;

  @GrpcClient("user-service")
  private UserServiceGrpc.UserServiceBlockingStub userStub;

  public TransferController(AccountRepository repo, KafkaTemplate<String,String> kafka, JwtUtil jwtUtil) {
    this.repo = repo; this.kafka = kafka; this.jwtUtil = jwtUtil;
  }

  @PostMapping("/transfer")
  @Transactional
  public Object transfer(@RequestHeader("Authorization") String auth, @RequestBody Map<String,String> body) {
    // auth => "Bearer <token>"
    String token = auth.replaceFirst("Bearer ", "").trim();
    String userId = jwtUtil.parseUserId(token);

    UUID fromAccountId = UUID.fromString(body.get("fromAccountId"));
    UUID toAccountId = UUID.fromString(body.get("toAccountId"));
    BigDecimal amount = new BigDecimal(body.get("amount"));

    var fromOpt = repo.findById(fromAccountId);
    var toOpt = repo.findById(toAccountId);
    if (fromOpt.isEmpty() || toOpt.isEmpty()) return Map.of("error","account not found");

    var from = fromOpt.get();
    var to = toOpt.get();

    // check owner
    if (!from.getUserId().toString().equals(userId)) {
      return Map.of("error","not owner of from account");
    }
    if (from.getBalance().compareTo(amount) < 0) {
      return Map.of("error","insufficient funds");
    }

    from.setBalance(from.getBalance().subtract(amount));
    to.setBalance(to.getBalance().add(amount));
    repo.save(from);
    repo.save(to);

    // call user-service via gRPC to enrich event (optional)
    UserResponse fromUser = null, toUser = null;
    try {
      fromUser = userStub.getUserById(UserRequest.newBuilder().setId(from.getUserId().toString()).build());
      toUser = userStub.getUserById(UserRequest.newBuilder().setId(to.getUserId().toString()).build());
    } catch(Exception e) {
      // ignore - best effort
    }

    String event = String.format("{\"fromAccount\":\"%s\",\"toAccount\":\"%s\",\"amount\":\"%s\",\"fromEmail\":\"%s\",\"toEmail\":\"%s\"}",
      from.getId(), to.getId(), amount.toPlainString(),
      fromUser != null ? fromUser.getEmail() : "", toUser != null ? toUser.getEmail() : ""
    );

    kafka.send("account.transfer", from.getId().toString(), event);

    return Map.of("status","ok");
  }
}
