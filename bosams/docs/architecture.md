# Architecture

- React SPA -> Nginx -> Spring Boot API -> PostgreSQL.
- JWT access + refresh for stateless auth.
- RBAC roles: ADMIN, PRINCIPAL, TEACHER only.
- Service-layer authorization enforces teacher assignment + active year constraints.
- Audit log records key CUD/security/mark events.
