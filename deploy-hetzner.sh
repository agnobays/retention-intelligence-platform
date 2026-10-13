#!/bin/bash
set -e

echo "========================================================================"
echo " 🚀 Deploying Retention Intelligence Platform to Hetzner Cloud (Ubuntu)"
echo "========================================================================"

# 1. Update system & install essential tools
echo "📦 [1/5] Updating packages and installing prerequisites..."
sudo apt-get update -y
sudo apt-get install -y ca-certificates curl gnupg lsb-release git

# 2. Install Docker and Docker Compose
echo "🐳 [2/5] Installing Docker Engine & Docker Compose plugin..."
if ! command -v docker &> /dev/null; then
    sudo install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    sudo chmod a+r /etc/apt/keyrings/docker.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update -y
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    sudo systemctl enable docker
    sudo systemctl start docker
fi

# 3. Create .env file if missing
echo "⚙️ [3/5] Configuring environment..."
if [ ! -f .env ]; then
    cat <<EOF > .env
POSTGRES_DB=retention_db
POSTGRES_USER=retention_user
POSTGRES_PASSWORD=retention_prod_pass_$(openssl rand -hex 8)
JWT_SECRET=$(openssl rand -hex 32)
EOF
fi

# 4. Build and start services via Docker Compose
echo "🚀 [4/5] Building and starting containers (Postgres, Spring Boot, Prometheus, Grafana)..."
docker compose up -d --build postgres backend prometheus grafana

# 5. Health check
echo "🔍 [5/5] Waiting for backend to become healthy..."
sleep 15
PUBLIC_IP=$(curl -s ifconfig.me || hostname -I | awk '{print $1}')

echo "========================================================================"
echo " 🎉 RETENTION INTELLIGENCE PLATFORM IS NOW LIVE!"
echo "========================================================================"
echo " Backend URL:     http://${PUBLIC_IP}:8080"
echo " Health Status:   http://${PUBLIC_IP}:8080/actuator/health"
echo " Grafana Monitor: http://${PUBLIC_IP}:3001 (admin / admin)"
echo ""
echo " 🌐 CLOUDFLARE CONFIGURATION:"
echo " In your Cloudflare DNS, add an 'A' record pointing to this server IP:"
echo "   Name:   api (or backend)"
echo "   Target: ${PUBLIC_IP}"
echo "   Proxy:  ON (Orange Cloud enabled)"
echo ""
echo " Then set VITE_API_BASE_URL on your Cloudflare Pages frontend to:"
echo "   https://api.yourdomain.com"
echo "========================================================================"
