# System Architecture Document

## 1. System Overview

The **Customer Retention Intelligence Platform** is built using a modern Java microservice architecture paired with a React single-page application and Camunda 8 workflow engine.

```
┌─────────────────────────────────────────────────────────┐
│                    React Frontend App                   │
│      (React 18 + Vite + TypeScript + Tailwind CSS)     │
└────────────────────────────┬────────────────────────────┘
                             │ REST API / JWT
                             ▼
┌─────────────────────────────────────────────────────────┐
│                Spring Boot 3 Backend API                │
│                     (Java 21 JDK)                       │
│  ┌───────────────────────┐   ┌───────────────────────┐  │
│  │ Security (JWT & RBAC) │   │ REST Controllers (12) │  │
│  └───────────┬───────────┘   └───────────┬───────────┘  │
│              │                           │              │
│  ┌───────────▼───────────┐   ┌───────────▼───────────┐  │
│  │     Business Engines  │   │  Spring Data JPA Repos│  │
│  └───────────┬───────────┘   └───────────┬───────────┘  │
└──────────────┼───────────────────────────┼──────────────┘
               │                           │
               ▼                           ▼
┌──────────────────────────┐    ┌──────────────────────────┐
│ Camunda 8 Zeebe Engine   │    │  PostgreSQL 16 Database  │
│ (BPMN Process Execution) │    │   (Flyway Schema V1)     │
└──────────────────────────┘    └──────────────────────────┘
```

---

## 2. Backend Architecture Layers

Follows a clean layered architecture pattern:

1. **Controller Layer (`com.retention.intelligence.controller`)**:
   Exposes RESTful endpoints, handles HTTP status codes, request validation, and OpenAPI documentation tags.
2. **Service Layer (`com.retention.intelligence.service`)**:
   Contains core domain logic, risk score calculations, decision matrix evaluation, and orchestration with Camunda Zeebe client.
3. **Repository Layer (`com.retention.intelligence.repository`)**:
   Spring Data JPA repositories mapping entity queries to PostgreSQL.
4. **Entity Layer (`com.retention.intelligence.entity`)**:
   JPA data models representing domain concepts (`Company`, `User`, `Customer`, `CustomerValueScore`, `AtRiskMetric`, `RecoveryPlan`, `AuditLog`, `Notification`).
5. **Security & Workflow Layer (`com.retention.intelligence.security` / `workflow`)**:
   Handles stateless JWT authentication filters, role-based authorization rules (`SUPER_ADMIN`, `COMPANY_ADMIN`, `MANAGER`, `ANALYST`), and Zeebe `@JobWorker` event handlers.

---

## 3. High Availability & Deployment Strategy

* **Containerization**: Backend and frontend services are containerized via Docker and orchestrated with Docker Compose for local development.
* **Database Migrations**: Flyway handles version-controlled database schema evolutions.
* **CI/CD**: GitHub Actions workflows continuously test backend Java code and build frontend production assets.
