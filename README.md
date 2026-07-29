# Retention Intelligence Platform

> **Enterprise Customer Retention Intelligence Platform**  
> Built with Java 21, Spring Boot 3, Camunda 7 Embedded BPMN Engine, PostgreSQL, Flyway, React, TypeScript, Vite, and Tailwind CSS.

---

## 📌 Overview

The **Customer Retention Intelligence Platform** (`retention-intelligence-platform`) is an enterprise-grade solution designed to identify churn risk early, evaluate customer lifetime value (LTV), trigger automated or manager-approved recovery processes via Camunda 7 BPMN workflows, and display actionable retention analytics on a modern executive dashboard.

---

## 🛠️ Technology Stack

### Backend
* **Language & Runtime**: Java 21 (JDK 21)
* **Framework**: Spring Boot 3.2.x
* **Build Tool**: Maven (`pom.xml`)
* **Security**: Spring Security 6 + JWT + RBAC (`SUPER_ADMIN`, `COMPANY_ADMIN`, `MANAGER`, `ANALYST`)
* **Persistence**: Spring Data JPA + Hibernate + PostgreSQL
* **Database Migrations**: Flyway (`db/migration/V1__init_schema.sql`)
* **Workflow Engine**: Camunda 7 Embedded (`camunda-bpm-spring-boot-starter-webapp` 7.21.0)
* **Camunda WebApp / Cockpit**: Integrated at `/camunda/app/` (Admin login: `admin` / `adminpassword`)
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

## ⚡ Quick Start

### Prerequisites
* Java 21 JDK
* Maven 3.8+
* Node.js 20+ & npm
* Docker & Docker Compose

### Running Locally for Development

#### 1. Start PostgreSQL Database
```bash
docker-compose up postgres -d
```

#### 2. Backend Setup
```bash
cd backend
mvn spring-boot:run
```
* **Backend API**: `http://localhost:8080/api/v1`
* **Swagger UI**: `http://localhost:8080/swagger-ui.html`
* **Camunda Cockpit UI**: `http://localhost:8080/camunda/app/` (User: `admin`, Pass: `adminpassword`)

#### 3. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
* **Frontend App**: `http://localhost:3000`

---

## 📄 License
Internal Enterprise License - All rights reserved.
