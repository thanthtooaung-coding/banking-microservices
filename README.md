Spring Boot Microservices Banking App
This is a sample banking application built with a microservices architecture using Spring Boot, Java 17, gRPC, Kafka, and PostgreSQL.

Core Features
User Service: Handles user registration and login (JWT-based).

Account Service: Manages user bank accounts.

Transaction Service: Orchestrates fund transfers between accounts.

Architecture & Technology
Microservices: user-service, account-service, transaction-service.

Backend: Java 17, Spring Boot 3.2.5, Spring Cloud 2023.0.3.

Database: Single PostgreSQL instance shared across services (with JPA/Hibernate).

Synchronous Communication: gRPC is used by the Transaction Service to fetch account details from the Account Service.
