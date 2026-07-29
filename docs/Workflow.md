# Camunda 8 BPMN Workflow Specification

## 1. Process Overview: `CustomerRecoveryProcess`

The customer recovery lifecycle is automated using **Camunda 8 (Zeebe)** workflow engine.

```
[ Customer Imported ]
         │
         ▼
[ Run Detection Engine ]  (Job Worker: run-detection-engine)
         │
         ▼
[ Evaluate Customer Value ]  (Job Worker: evaluate-customer-value)
         │
         ▼
[ Recommend Recovery Action ]  (Job Worker: recommend-recovery-action)
         │
         ├── (requiresApproval = true)  ──► [ Manager Approval ] (User Task)
         │                                       │
         └── (requiresApproval = false) ─────────┼──────┐
                                                        │
                                                        ▼
[ Execute Recovery Action ]  (Job Worker: execute-recovery-action)
         │
         ▼
[ Wait for Outcome ]  (Timer Event: 30 Days)
         │
         ▼
[ Close Recovery Case ]  (Job Worker: close-recovery-case)
         │
         ▼
[ Case Closed End Event ]
```

---

## 2. Zeebe Job Workers

Defined in `com.retention.intelligence.workflow.CustomerRecoveryWorkflowWorker`:
1. `run-detection-engine`
2. `evaluate-customer-value`
3. `recommend-recovery-action`
4. `execute-recovery-action`
5. `close-recovery-case`
