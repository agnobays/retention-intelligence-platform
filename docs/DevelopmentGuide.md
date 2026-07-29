# Intern Developer Onboarding Guide

Welcome to the **Customer Retention Intelligence Platform** project! This guide will help you get your local environment set up, understand the codebase, and start implementing features.

---

## 🚀 1. Local Environment Setup

### Prerequisites
* **Java 21**: Verify with `java -version`
* **Maven 3.8+**: Verify with `mvn -version`
* **Node.js 20+ & npm**: Verify with `node -v`
* **Docker**: Verify with `docker --version`

### Step-by-Step Setup
1. **Clone & Checkout Develop Branch**:
   ```bash
   git checkout develop
   ```
2. **Start PostgreSQL Database**:
   ```bash
   docker-compose up postgres -d
   ```
3. **Run Backend Service**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   * **API Swagger UI**: `http://localhost:8080/swagger-ui.html`
   * **Camunda Cockpit Dashboard**: `http://localhost:8080/camunda/app/cockpit/` (Admin login: `admin` / `adminpassword`)
4. **Run Frontend Application**:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   * Open `http://localhost:3000` in your browser.

---

## 👩‍💻 2. How to Add a New Workflow Delegate

To create a new task in Camunda 7:
1. Create a Spring component class in `com.retention.intelligence.workflow.delegates`:
   ```java
   @Component("myNewTaskDelegate")
   public class MyNewTaskDelegate implements JavaDelegate {
       @Override
       public void execute(DelegateExecution execution) throws Exception {
           // Your business logic here
       }
   }
   ```
2. Open `src/main/resources/bpmn/CustomerRecoveryProcess.bpmn` in Camunda Modeler and set the Service Task implementation to:
   - **Type**: `Delegate Expression`
   - **Delegate Expression**: `${myNewTaskDelegate}`
