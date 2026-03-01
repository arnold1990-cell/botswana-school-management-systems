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


## Flyway checksum mismatch recovery (safe / data-preserving)
If backend startup fails with `FlywayValidateException` and a checksum mismatch for an already-applied migration (for example `V3`), do **not** edit that migration again.

PowerShell commands (from repo root):
```powershell
cd .\backend
$env:DB_URL="jdbc:postgresql://localhost:5432/bosams"
$env:DB_USER="bosams"
$env:DB_PASSWORD="bosams"
mvn -Dflyway.url=$env:DB_URL -Dflyway.user=$env:DB_USER -Dflyway.password=$env:DB_PASSWORD flyway:repair
```

Then start the backend:
```powershell
mvn spring-boot:run
```

If this is disposable local data, reset instead:
```powershell
docker compose -f docker-compose.dev.yml down -v
```

## API login verification
```powershell
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" -ContentType "application/json" -Body '{"email":"admin@bosams.local","password":"password"}'
$token = $login.accessToken
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/users/me" -Headers @{ Authorization = "Bearer $token" }
```
