# Database & Schema Specifications

## 1. Relational Schema (`PostgreSQL 16`)

Managed via **Flyway** in `backend/src/main/resources/db/migration/V1__init_schema.sql`.

### Core Tables Overview
* `companies`: Multi-tenant organization boundaries.
* `users`: User profiles with roles (`SUPER_ADMIN`, `COMPANY_ADMIN`, `MANAGER`, `ANALYST`).
* `customers`: Imported customer profiles, ARR/MRR, health scores, and statuses.
* `customer_value_scores`: LTV, SLA tiers, and support load statistics.
* `at_risk_metrics`: Telemetry risk signals (e.g. usage drop, payment failure).
* `recovery_plans`: Recommended actions, discounts, workflow IDs, and approval statuses.
* `audit_logs`: Audit trail for compliance tracking.
* `notifications`: User alert notifications.

---

## 2. Flyway Migration Workflow

To add new database migrations:
1. Create SQL file in `backend/src/main/resources/db/migration/` following naming convention: `V2__add_new_feature_table.sql`.
2. Run `mvn compile` or start Spring Boot application. Flyway will validate and apply pending migrations automatically.
