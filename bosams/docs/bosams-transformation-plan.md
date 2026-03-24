# BOSAMS Transformation Audit, Gap Analysis, and Delivery Plan

## 1. Audit summary of the existing codebase

### Backend (Spring Boot, Java 21, Maven)
- Existing modules are centered on authentication, users, academics baseline (year/term/task), teacher assignment, marks entry, exam schedules, reports, and audit logs.
- Current REST surface includes `/api/auth`, `/api/users`, `/api/learners`, `/api/subjects`, `/api/teacher...`, `/api/marks`, `/api/reports`, `/api/exam-schedules`, and `/api/academic-years`.
- Architecture is layered and mostly consistent with Controller -> Service -> Repository.
- JWT access + refresh auth already exists with persisted refresh token revocation and audit logging for auth events.
- DB schema currently models users, students, standards/streams/subjects, academic years/terms, exam groups/schedules, marks, teacher profiles/assignments, refresh tokens, and audit logs.
- Global exception handling and validation are present in key modules.

### Frontend (React + Vite + TypeScript)
- Existing authenticated SPA with protected routes and role-aware navigation.
- Implemented pages focus on dashboard, teachers, learners, subjects, marks entry, and reports.
- Existing role model was limited to ADMIN/PRINCIPAL/TEACHER before this change set.
- UI already follows a light dashboard + sidebar + top bar pattern and can be extended into BOSAMS modules.

### Tests / CI alignment
- Backend has unit + integration tests for auth, authorization, academics, marks, repositories, and controllers.
- Frontend includes Vite build and Playwright e2e baseline.
- Existing command flow supports Maven verify/build and npm build.

## 2. Gap analysis versus BOSAMS requirements

### Fully existing or strongly present
- JWT authentication core (login/refresh/logout).
- RBAC foundation (currently partial role set).
- Learners/subjects/teacher assignment basics.
- Academic year/term/task foundations.
- Exam + marks + reporting baseline.
- Audit logging baseline.

### Partially existing
- Role dashboards (admin and teacher skeletons exist; no full KPIs for all required roles).
- SIS scope (student basics exist, parent/guardian/class hierarchy depth incomplete).
- LMS scope (subject/marks foundation exists, but lessons/topics/attachments/submissions incomplete).
- Attendance, timetable, holidays, announcements are either minimal or missing deep workflow.
- Finance/session-year/fee lifecycle is largely missing.
- Messaging/notification center not fully implemented.

### Missing or minimal
- Mandatory role coverage for STUDENT/PARENT/ACCOUNTANT end-to-end.
- Email confirmation and password-reset-by-email workflows.
- Parent portal and mobile-first student/parent experience.
- Full academics master data (medium/section/shift/class mappings and assignment matrices).
- Fees/payments/transactions/receipts/reporting modules.
- Library/resources module.
- Bulk import and roll-number workflows.
- AI assistance layer (optional).

### Technical debt / blockers
- Domain model is exam-centric and must be broadened without breaking existing contracts.
- Frontend routing/menu still reflects current limited modules.
- Need careful migration strategy for new entities and role expansion.

## 3. Phase-by-phase implementation plan

### Phase 1 (completed in this change): Audit baseline
- Captured current backend/frontend capabilities and constraints.
- Mapped reusable modules and identified missing BOSAMS capabilities.

### Phase 2 (completed in this change): Gap analysis baseline
- Classified fully existing, partial, and missing modules.
- Identified migration and extensibility hotspots.

### Phase 3 (started in this change): Authentication/RBAC extension groundwork
- Expanded platform role enum/types to include STUDENT, PARENT, ACCOUNTANT.
- Extended frontend route protection and role-based dashboard entry points.

### Phase 4–10 (planned)
- Phase 4: SIS entities/APIs (class-medium-section-stream, student-parent links, roll management).
- Phase 5: Attendance/timetable/holiday operations.
- Phase 6: LMS lessons/topics/assignments/submissions/resources.
- Phase 7: Exams/results/report-card and export expansion.
- Phase 8: Announcements/messages/notifications/email integration.
- Phase 9: Fees/session-year/installments/payments/transactions/receipts.
- Phase 10: UI polish/charts/mobile UX/analytics/optional AI and seeded data.

## 4. Exact backend changes

- Extended `Enums.Role` with:
  - `STUDENT`
  - `PARENT`
  - `ACCOUNTANT`
- Kept existing auth and endpoint behavior unchanged to preserve backward compatibility while enabling phase-by-phase onboarding of additional roles.

## 5. Exact frontend changes

- Extended frontend role type union to include `STUDENT`, `PARENT`, `ACCOUNTANT`.
- Added new role dashboards:
  - `StudentDashboardPage`
  - `ParentDashboardPage`
  - `AccountantDashboardPage`
- Added protected routes:
  - `/student/dashboard`
  - `/parent/dashboard`
  - `/accountant/dashboard`
- Enhanced role fallback routing in `RoleProtectedRoute` for all supported roles.
- Updated `AppLayout` navigation rendering to show role-specific sidebar items for new roles.

## 6. Database/entity changes

- No schema migration added in this change.
- Existing `users.role` column is `VARCHAR`, so role expansion is compatible with upcoming seeded users and API extensions.

## 7. API additions/changes

- No endpoint signatures changed in this change.
- Existing API compatibility preserved.
- Role expansion lays groundwork for future student/parent/accountant API access control per module.

## 8. Security/authentication changes

- No change to JWT flow, password hashing, refresh-token revocation, or authentication filters.
- Role enum expanded so new authenticated users can be represented securely in the domain model and frontend route guards.

## 9. Testing updates

- Verified frontend TypeScript/Vite build after route and role changes.
- Attempted backend Maven verification; execution is currently blocked in this environment by a remote dependency resolution `403 Forbidden` from Maven Central.

## 10. Important assumptions where requirements are ambiguous

- This increment prioritizes non-breaking foundational RBAC extension before introducing large schema additions.
- Student/parent/accountant dashboards are initial production-safe placeholders pending full module delivery in subsequent phases.
- Finance, attendance, timetable, and LMS deep workflows are planned as phased additions to avoid destabilizing current exam/marks functionality.

## 11. Final checklist showing which BOSAMS features were matched

### Matched in this increment
- [x] Audit-first approach.
- [x] Gap analysis documented.
- [x] Phase-based implementation plan documented.
- [x] Expanded role model for required BOSAMS actors (domain + frontend).
- [x] Added role-specific dashboard entry points for STUDENT/PARENT/ACCOUNTANT.
- [x] Preserved existing auth flow and existing endpoints.
- [x] Kept architecture modular and non-breaking.

### Not yet fully matched (scheduled for next phases)
- [ ] Email confirmation + password reset by email workflows.
- [ ] Full academics master-data + assignment matrices.
- [ ] Full SIS admissions/guardians/bulk import/roll assignment.
- [ ] Attendance/timetable/holiday complete modules.
- [ ] LMS lessons/topics/attachments/submission grading.
- [ ] Finance/session year/payments/receipts/transactions.
- [ ] Messaging/notifications/reporting breadth.
- [ ] Mobile portal parity and AI features.
