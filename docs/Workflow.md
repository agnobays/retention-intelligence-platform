# Camunda 7 BPMN Workflow Specification

## 1. Process Overview: `CustomerRecoveryProcess`

The customer recovery lifecycle is automated using the embedded **Camunda 7** workflow engine.

```
[ Customer Imported ]
         │
         ▼
[ Run Detection Engine ]  (JavaDelegate: runDetectionEngineDelegate)
         │
         ▼
[ Evaluate Customer Value ]  (JavaDelegate: evaluateCustomerValueDelegate)
         │
         ▼
[ Recommend Recovery Action ]  (JavaDelegate: recommendRecoveryActionDelegate)
         │
         ├── (requiresApproval == true) ──► [ Manager Approval ] (User Task)
         │                                       │
         └── (requiresApproval == false) ────────┼──────┐
                                                        │
                                                        ▼
[ Execute Recovery Action ]  (JavaDelegate: executeRecoveryActionDelegate)
         │
         ▼
[ Wait for Outcome ]  (Timer Event: 30 Days)
         │
         ▼
[ Close Recovery Case ]  (JavaDelegate: closeRecoveryCaseDelegate)
         │
         ▼
[ Case Closed End Event ]
```

---

## 2. Spring Java Delegates

Located in `com.retention.intelligence.workflow.delegates`:
1. `runDetectionEngineDelegate`
2. `evaluateCustomerValueDelegate`
3. `recommendRecoveryActionDelegate`
4. `executeRecoveryActionDelegate`
5. `closeRecoveryCaseDelegate`
