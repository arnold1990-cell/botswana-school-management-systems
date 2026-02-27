# Bosams School Management System (bosams)

Production-ready full-stack monorepo for school operations with strict RBAC.

## Demo credentials
All demo users use password: `password`
- admin@bosams.local (ADMIN)
- principal@bosams.local (PRINCIPAL)
- teacher@bosams.local (TEACHER)

## Happy path
1. Create academic year and set active.
2. Create standards, streams, subjects, students.
3. Create teacher assignments (active year).
4. Create exam group and schedules.
5. Enter marks and submit.
6. Generate reports/PDF.

## Run with Docker
```bash
docker compose -f docker-compose.dev.yml up --build
```
- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html
