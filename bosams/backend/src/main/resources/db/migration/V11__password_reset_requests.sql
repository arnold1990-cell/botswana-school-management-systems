CREATE TABLE IF NOT EXISTS password_reset_request (
    id BIGSERIAL PRIMARY KEY,
    admission_no VARCHAR(60) NOT NULL,
    student_name VARCHAR(150) NOT NULL,
    guardian_email VARCHAR(120),
    guardian_phone VARCHAR(20),
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admin_note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    resolved_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_password_reset_request_status_created
    ON password_reset_request (status, created_at DESC);
