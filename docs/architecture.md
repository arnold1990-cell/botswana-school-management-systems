# BoSAMS Phase-1 Architecture

## Modular Monolith Boundaries
- `com.bosams.common`: shared primitives only (auditing base class, exception handling).
- `com.bosams.identity`: placeholder package for future identity/access module.
- `com.bosams.schoolsetup`: school setup and master data module (entities, repositories, DTOs, API).

The initial implementation keeps modules in one Spring Boot deployment with package boundaries and no circular dependencies.

## Multi-school (`school_id`) Strategy
- Phase-1 uses one database with logical multi-tenancy.
- `School` is the tenant root.
- All school-owned entities include `school_id` and are validated/queried by school scope.
- Full tenant routing/context propagation is intentionally deferred.

## Data Strategy
- PostgreSQL target DB.
- Flyway migration creates schema and dev seed data.
- Audit columns (`created_at`, `updated_at`, `created_by`, `updated_by`) are included on all tables.
