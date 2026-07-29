# System Architecture Document

## 1. System Overview

The **Customer Retention Intelligence Platform** is built using a Java microservice architecture with an **Embedded Camunda 7 Workflow Engine** inside Spring Boot 3.

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
│  │     Business Service  │   │  Camunda 7 Engine     │  │
│  │     & JavaDelegates   │   │ (Cockpit / WebApp)    │  │
│  └───────────┬───────────┘   └───────────┬───────────┘  │
└──────────────┼───────────────────────────┼──────────────┘
               │                           │
               └─────────────┬─────────────┘
                             ▼
                ┌──────────────────────────┐
                │  PostgreSQL 16 Database  │
                │ (Flyway Schema & Camunda)│
                └──────────────────────────┘
```

---

## 2. Backend Architecture & Camunda 7 Embedded Integration

1. **Embedded Workflow Engine**:
   Camunda 7 runs directly within the Spring Boot JVM via `camunda-bpm-spring-boot-starter-webapp`. It uses the shared PostgreSQL database connection for workflow runtime and history tables (`ACT_RU_*` and `ACT_HI_*`).
2. **Java Delegates (`com.retention.intelligence.workflow.delegates`)**:
   Workflows trigger Spring-managed `@Component` delegates implementing `org.camunda.bpm.engine.delegate.JavaDelegate`.
3. **Camunda WebApp & Cockpit**:
   Built-in process monitoring dashboard available at `/camunda/app/cockpit/`.
