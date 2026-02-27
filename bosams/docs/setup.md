# Local setup

## Docker Compose
```bash
docker compose -f docker-compose.dev.yml up --build
```

## Backend manual
```bash
cd backend
mvn spring-boot:run
```

## Frontend manual
```bash
cd frontend
npm install
npm run dev
```
