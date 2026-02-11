# Backend coherence summary

## Duplicate entities removed/disabled
- Removed the entire `com.bosams.schoolsetup.domain.model` JPA tree and the V2 service/controller/repository stack that depended on it.
- Consolidated school setup persistence on `com.bosams.schoolsetup.domain` entities only.

## ID strategy chosen
- Standardized on `Long` IDs with `GenerationType.IDENTITY` (BIGSERIAL/BIGINT in Flyway).
- This matches existing Flyway baseline tables (`V1__school_setup.sql`, `V3__learner_parent_module.sql`, `V4__hr_module.sql`) and the majority of current entities.

## Flyway migrations added/changed
- Added `V5__school_scope_consistency.sql`:
  - drops stray `academic_year` table (if manually created with conflicting shape),
  - enforces per-school uniqueness constraints on school-owned master-data tables.

## How to run
1. Ensure PostgreSQL is running and datasource env vars are configured.
2. Run from `backend/`:
   - `mvn spring-boot:run`
3. Flyway will apply migrations `V1`..`V5`.
