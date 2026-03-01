CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(160) UNIQUE NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE standard (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) UNIQUE NOT NULL
);

CREATE TABLE stream (
    id BIGSERIAL PRIMARY KEY,
    standard_id BIGINT NOT NULL REFERENCES standard (id),
    name VARCHAR(120) NOT NULL,
    UNIQUE (standard_id, name)
);

CREATE TABLE subject (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) UNIQUE NOT NULL
);

CREATE TABLE student (
    id BIGSERIAL PRIMARY KEY,
    admission_no VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(120) NOT NULL,
    last_name VARCHAR(120) NOT NULL,
    gender VARCHAR(20),
    stream_id BIGINT NOT NULL REFERENCES stream (id),
    status VARCHAR(20)
);

CREATE TABLE academic_year (
    id BIGSERIAL PRIMARY KEY,
    label VARCHAR(20) UNIQUE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE term (
    id BIGSERIAL PRIMARY KEY,
    academic_year_id BIGINT NOT NULL REFERENCES academic_year (id),
    term_no INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);

CREATE TABLE exam_group (
    id BIGSERIAL PRIMARY KEY,
    academic_year_id BIGINT NOT NULL REFERENCES academic_year (id),
    term_id BIGINT REFERENCES term (id),
    name VARCHAR(120) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE exam_schedule (
    id BIGSERIAL PRIMARY KEY,
    exam_group_id BIGINT NOT NULL REFERENCES exam_group (id),
    stream_id BIGINT NOT NULL REFERENCES stream (id),
    subject_id BIGINT NOT NULL REFERENCES subject (id),
    exam_datetime TIMESTAMP NOT NULL,
    max_marks NUMERIC(5, 2) NOT NULL,
    mark_entry_last_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE mark (
    id BIGSERIAL PRIMARY KEY,
    exam_schedule_id BIGINT NOT NULL REFERENCES exam_schedule (id),
    student_id BIGINT NOT NULL REFERENCES student (id),
    marks NUMERIC(5, 2),
    absent BOOLEAN NOT NULL DEFAULT FALSE,
    grade VARCHAR(5),
    result VARCHAR(20),
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    locked_at TIMESTAMP,
    UNIQUE (exam_schedule_id, student_id)
);

CREATE TABLE grade_profile (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    rules_json TEXT NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE teacher_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID UNIQUE NOT NULL REFERENCES users (id),
    staff_no VARCHAR(100) UNIQUE,
    phone VARCHAR(100)
);

CREATE TABLE teacher_assignment (
    id BIGSERIAL PRIMARY KEY,
    teacher_user_id UUID NOT NULL REFERENCES users (id),
    academic_year_id BIGINT NOT NULL REFERENCES academic_year (id),
    stream_id BIGINT NOT NULL REFERENCES stream (id),
    subject_id BIGINT NOT NULL REFERENCES subject (id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (teacher_user_id, academic_year_id, stream_id, subject_id)
);

CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    actor_user_id UUID,
    action VARCHAR(100) NOT NULL,
    entity VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    before_json TEXT,
    after_json TEXT,
    note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE refresh_token (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id),
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_student_admission_no ON student (admission_no);
CREATE INDEX idx_mark_schedule_student ON mark (exam_schedule_id, student_id);
CREATE INDEX idx_teacher_assignment_teacher_year ON teacher_assignment (teacher_user_id, academic_year_id);
CREATE INDEX idx_exam_schedule_group_stream_subject ON exam_schedule (exam_group_id, stream_id, subject_id);
