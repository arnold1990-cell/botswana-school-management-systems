# Deploy on Ubuntu VPS
1. Install Docker and Compose plugin.
2. Clone repo and set `.env` with `JWT_SECRET`.
3. Run:
```bash
docker compose -f docker-compose.prod.yml up -d --build
```
4. Put TLS in front (Caddy/Nginx + certbot).
