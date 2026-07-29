# Intern Developer Onboarding Guide

Welcome to the **Customer Retention Intelligence Platform** project! This guide will help you get your local environment set up, understand the codebase, and start implementing features.

---

## 🚀 1. Local Environment Setup

### Prerequisites
* **Java 21**: Verify with `java -version`
* **Maven 3.8+**: Verify with `mvn -version`
* **Node.js 20+ & npm**: Verify with `node -v`
* **Docker & Docker Compose**: Verify with `docker compose version`

### Step-by-Step Setup
1. **Clone & Checkout Develop Branch**:
   ```bash
   git checkout develop
   ```
2. **Start Local Database & Camunda 8 Zeebe Broker**:
   ```bash
   docker-compose up postgres zeebe -d
   ```
3. **Run Backend Service**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   * Swagger UI will be available at `http://localhost:8080/swagger-ui.html`.
4. **Run Frontend Application**:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   * Open `http://localhost:3000` in your browser.

---

## 👩‍💻 2. Git Workflow for Feature Work

* **Never commit directly to `main` or `develop`.**
* Create a feature branch off `develop`:
  ```bash
  git checkout develop
  git pull origin develop
  git checkout -b feature/your-feature-name
  ```
* Naming conventions for branches:
  * `feature/authentication`
  * `feature/customer-management`
  * `feature/detection-engine`
  * `feature/decision-engine`
  * `feature/dashboard`
* Push your feature branch and open a Pull Request (PR) into `develop`.

---

## 🏗️ 3. How to Implement a New Feature Module

### Adding a Backend Feature
1. Add entity properties in `entity/` if needed.
2. Add Flyway SQL script in `db/migration/V2__your_change.sql`.
3. Add repository methods in `repository/`.
4. Define DTO request/response payload in `dto/`.
5. Implement business logic in `service/`.
6. Expose REST endpoint in `controller/` with `@Operation` OpenAPI annotations.
7. Write unit tests in `src/test/java/`.

### Adding a Frontend Feature
1. Define TypeScript interface in `src/types/index.ts`.
2. Add API call method in `src/services/`.
3. Build modular UI component in `src/components/`.
4. Create or update page in `src/pages/`.
5. Connect route in `src/App.tsx`.

---

## 🧪 4. Testing Guidelines

* **Backend Unit Tests**: Run `mvn test` inside `backend/`.
* **Frontend Verification**: Run `npm run build` and `npm run lint` inside `frontend/`.
