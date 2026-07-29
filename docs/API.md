# REST API Specifications

Swagger Interactive UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI JSON Endpoint: `http://localhost:8080/v3/api-docs`

---

## 🔐 1. Authentication
* **POST `/api/v1/auth/login`**: Authenticate user and return JWT bearer token.

## 🏢 2. Company Management
* **GET `/api/v1/companies`**: List tenant companies (`SUPER_ADMIN`, `COMPANY_ADMIN`).

## 👥 3. User Management
* **GET `/api/v1/users/company/{companyId}`**: Retrieve users by company ID.

## 👤 4. Customer Management
* **GET `/api/v1/customers/company/{companyId}`**: Get customer list.
* **POST `/api/v1/customers/import`**: Ingest/import customer record.

## 🔍 5. Detection Engine
* **POST `/api/v1/detection/evaluate/{customerId}`**: Run risk calculation and metric evaluation.

## 💰 6. Customer Value Engine
* **POST `/api/v1/value-engine/calculate/{customerId}`**: Compute LTV, usage frequency, and SLA tier.

## 🧠 7. Decision Engine
* **POST `/api/v1/decision-engine/recommend/{customerId}`**: Get recommended recovery action playbook.

## 🚀 8. Recovery Engine
* **POST `/api/v1/recovery-engine/execute/{planId}`**: Execute recovery plan action.

## 🔌 9. Integration Engine
* **POST `/api/v1/integration/webhook/{sourceSystem}`**: Receive incoming CRM telemetry webhooks.

## 📊 10. Reporting
* **GET `/api/v1/reports/dashboard`**: Fetch executive KPI retention dashboard metrics.

## 🔔 11. Notifications
* **GET `/api/v1/notifications/user/{userId}`**: Fetch unread alert notifications.

## 🔄 12. Workflow (Camunda 8)
* **POST `/api/v1/workflow/start/{customerId}`**: Trigger `CustomerRecoveryProcess` BPMN instance.
