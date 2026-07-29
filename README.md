# Retention Intelligence Platform

> **Enterprise Customer Retention Intelligence Platform**  
> Built with Java 21, Spring Boot 3, Camunda 8 BPMN Workflow Engine, PostgreSQL, Flyway, React, TypeScript, Vite, and Tailwind CSS.

---

## 📌 Overview

The **Customer Retention Intelligence Platform** (`retention-intelligence-platform`) is an enterprise-grade solution designed to identify churn risk early, evaluate customer lifetime value (LTV), trigger automated or manager-approved recovery processes via Camunda 8 workflows, and display actionable retention analytics on a modern executive dashboard.

---

## 🛠️ Technology Stack

### Backend
* **Language & Runtime**: Java 21 (JDK 21)
* **Framework**: Spring Boot 3.2.x
* **Build Tool**: Maven (`pom.xml`)
* **Security**: Spring Security 6 + JWT + RBAC (`SUPER_ADMIN`, `COMPANY_ADMIN`, `MANAGER`, `ANALYST`)
* **Persistence**: Spring Data JPA + Hibernate + PostgreSQL
* **Database Migrations**: Flyway (`db/migration/V1__init_schema.sql`)
* **Workflow Engine**: Camunda 8 (Zeebe Spring Client + BPMN 2.0)
* **API Documentation**: OpenAPI 3 / Swagger UI (`/swagger-ui.html`)

### Frontend
* **Core**: React 18 + Vite + TypeScript
* **Styling**: Tailwind CSS + Custom Dark/Glassmorphic Design System
* **State & Data Fetching**: TanStack Query (React Query) + Axios
* **Routing**: React Router v6
* **Icons & Animation**: Lucide React + Framer Motion

### Infrastructure & DevOps
* **Containers**: Docker & Docker Compose
* **Web Server / Reverse Proxy**: Nginx
* **CI/CD**: GitHub Actions workflows (`.github/workflows/`)

---

## 📁 Repository Structure

```
retention-intelligence-platform/
├── backend/                  # Java 21 Spring Boot 3 Microservice
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/retention/intelligence/
│       │   │   ├── config/       # Spring Security, Camunda, OpenAPI configs
│       │   │   ├── controller/   # REST API Controllers (12 Modules)
│       │   │   ├── service/      # Business logic & Camunda workers
│       │   │   ├── repository/   # Spring Data JPA repositories
│       │   │   ├── entity/       # JPA entities
│       │   │   ├── dto/          # Data Transfer Objects
│       │   │   ├── mapper/       # DTO converters
│       │   │   ├── security/     # JWT filters & role definitions
│       │   │   ├── workflow/     # Camunda Zeebe workers & BPMN service interfaces
│       │   │   ├── integration/  # External integration connectors
│       │   │   ├── exception/    # Global Exception Handler & error responses
│       │   │   └── util/         # Utility helpers & risk calculators
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── db/migration/  # Flyway schema migrations
│       │       └── bpmn/          # CustomerRecoveryProcess.bpmn workflow
│       └── test/                 # Unit & Integration tests
├── frontend/                 # React Vite TypeScript Tailwind App
│   ├── package.json
│   ├── vite.config.ts
│   ├── tailwind.config.js
│   └── src/
│       ├── components/       # UI Component library (Cards, Tables, Modals, Badges)
│       ├── pages/            # 8 Module Pages (Dashboard, Customers, Recovery, etc.)
│       ├── layouts/          # Dashboard & Auth layouts
│       ├── services/         # Axios API clients
│       ├── types/            # TypeScript interfaces & models
│       └── contexts/         # Authentication & UI Contexts
├── docs/                     # Comprehensive Architecture & Developer Guides
│   ├── Architecture.md
│   ├── API.md
│   ├── Workflow.md
│   ├── Database.md
│   └── DevelopmentGuide.md
├── database/                 # Database initialization scripts
│   └── init.sql
├── docker/                   # Container definitions & Nginx config
│   ├── backend.Dockerfile
│   ├── frontend.Dockerfile
│   └── nginx.conf
├── .github/workflows/        # GitHub Actions CI pipelines
│   ├── ci-backend.yml
│   └── ci-frontend.yml
└── docker-compose.yml        # Orchestration for local development
```

---

## ⚡ Quick Start

### Prerequisites
* Java 21 JDK
* Maven 3.8+
* Node.js 18+ & npm
* Docker & Docker Compose

### Running with Docker Compose
```bash
docker-compose up --build -d
```
* **Frontend App**: `http://localhost:3000`
* **Backend API**: `http://localhost:8080/api/v1`
* **Swagger UI**: `http://localhost:8080/swagger-ui.html`

### Running Locally for Development

#### 1. Start Database & Infrastructure
```bash
docker-compose up postgres zeebe -d
```

#### 2. Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### 3. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

---

## 📄 License
Internal Enterprise License - All rights reserved.
