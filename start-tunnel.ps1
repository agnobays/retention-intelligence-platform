# Cloudflare Tunnel Launcher for Retention Intelligence Backend
# Exposes local Spring Boot (http://localhost:8080) to a secure public HTTPS URL

$cloudflaredPath = "C:\Program Files (x86)\cloudflared\cloudflared.exe"

if (-not (Test-Path $cloudflaredPath)) {
    Write-Host "Searching for cloudflared in PATH..." -ForegroundColor Cyan
    $cloudflaredPath = (Get-Command cloudflared -ErrorAction SilentlyContinue).Source
}

if (-not $cloudflaredPath) {
    Write-Error "cloudflared is not found. Run 'winget install Cloudflare.cloudflared' first."
    exit 1
}

Write-Host "=====================================================================" -ForegroundColor Green
Write-Host " 🚀 Launching Cloudflare Tunnel for Retention Platform Backend (:8080)" -ForegroundColor Green
Write-Host "=====================================================================" -ForegroundColor Green
Write-Host "Copy the generated *.trycloudflare.com URL and set it as your frontend" -ForegroundColor Yellow
Write-Host "environment variable: VITE_API_BASE_URL in Cloudflare Pages." -ForegroundColor Yellow
Write-Host "=====================================================================`n" -ForegroundColor Green

& $cloudflaredPath tunnel --url http://localhost:8080
