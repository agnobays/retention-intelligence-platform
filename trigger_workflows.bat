@echo off
echo ====================================================================
echo   Standard Bank CIB - Trigger Camunda 7 BPMN Workflows (Render API)
echo ====================================================================
echo.

SET BACKEND_URL=https://retention-intelligence-backend.onrender.com/api/v1/workflow
SET SHOPRITE_ID=a1111111-1111-1111-1111-111111111111

echo [1/3] Triggering CustomerRecoveryProcess...
curl -X POST "%BACKEND_URL%/start/%SHOPRITE_ID%" -H "Content-Type: application/json"
echo.
echo.

echo [2/3] Triggering ExecutiveEscalationProcess...
curl -X POST "%BACKEND_URL%/start-executive-escalation/%SHOPRITE_ID%" -H "Content-Type: application/json"
echo.
echo.

echo [3/3] Triggering ChurnPreventionSurveyProcess...
curl -X POST "%BACKEND_URL%/start-churn-survey/%SHOPRITE_ID%" -H "Content-Type: application/json"
echo.
echo.

echo ====================================================================
echo ✅ All 3 Camunda Workflows Triggered! Check Camunda Cockpit:
echo    https://retention-intelligence-backend.onrender.com/camunda/app/cockpit/default/
echo ====================================================================
pause
