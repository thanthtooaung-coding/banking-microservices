package com.example.user.grpc;

import com.example.proto.UserRequest;
import com.example.proto.UserResponse;
import com.example.proto.UserServiceGrpc;
import com.example.user.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
  private final UserRepository repo;
  public UserGrpcService(UserRepository repo) { this.repo = repo; }

  @Override
  public void getUserById(UserRequest request, StreamObserver<UserResponse> responseObserver) {
    var id = UUID.fromString(request.getId());
    var opt = repo.findById(id);
    if (opt.isEmpty()) {
      responseObserver.onError(Status.NOT_FOUND.withDescription("user not found").asRuntimeException());
      return;
    }
    var u = opt.get();
    var resp = UserResponse.newBuilder().setId(u.getId().toString()).setEmail(u.getEmail()).build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
}
