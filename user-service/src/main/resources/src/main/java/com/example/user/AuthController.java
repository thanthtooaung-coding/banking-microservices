package com.example.user;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserRepository repo;
  private final KafkaTemplate<String,String> kafka;
  private final JwtUtil jwtUtil;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthController(UserRepository repo, KafkaTemplate<String,String> kafka, JwtUtil jwtUtil) {
    this.repo = repo; this.kafka = kafka; this.jwtUtil = jwtUtil;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String,String> body){
    String email = body.get("email");
    String password = body.get("password");
    if (repo.findByEmail(email).isPresent()) {
      return ResponseEntity.badRequest().body("email already exists");
    }
    User u = new User();
    u.setEmail(email);
    u.setPassword(encoder.encode(password));
    repo.save(u);

    // produce Kafka event user.created (simple JSON)
    String payload = String.format("{\"id\":\"%s\",\"email\":\"%s\"}", u.getId().toString(), u.getEmail());
    kafka.send("user.created", u.getId().toString(), payload);

    return ResponseEntity.status(201).body(Map.of("id", u.getId(), "email", u.getEmail()));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String,String> body){
    String email = body.get("email");
    String password = body.get("password");
    var opt = repo.findByEmail(email);
    if (opt.isEmpty() || !encoder.matches(password, opt.get().getPassword())) {
      return ResponseEntity.status(401).body("invalid");
    }
    String token = jwtUtil.generateToken(opt.get().getId().toString());
    return ResponseEntity.ok(Map.of("token", token));
  }
}
