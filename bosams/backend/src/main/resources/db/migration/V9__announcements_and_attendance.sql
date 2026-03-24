CREATE TABLE IF NOT EXISTS announcement (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(180) NOT NULL,
    message TEXT NOT NULL,
    target_role VARCHAR(30),
    target_grade_level INT,
    created_by_user_id UUID REFERENCES users (id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at TIMESTAMPTZ,
    CONSTRAINT chk_announcement_target_role CHECK (target_role IN ('ADMIN', 'PRINCIPAL', 'TEACHER', 'STUDENT', 'PARENT', 'ACCOUNTANT') OR target_role IS NULL),
    CONSTRAINT chk_announcement_grade CHECK (target_grade_level BETWEEN 1 AND 12 OR target_grade_level IS NULL)
);

CREATE INDEX IF NOT EXISTS idx_announcement_target_role ON announcement (target_role);
CREATE INDEX IF NOT EXISTS idx_announcement_created_at ON announcement (created_at DESC);

CREATE TABLE IF NOT EXISTS attendance_record (
    id BIGSERIAL PRIMARY KEY,
    attendance_date DATE NOT NULL,
    student_id BIGINT NOT NULL REFERENCES student (id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    remark VARCHAR(300),
    marked_by_user_id UUID REFERENCES users (id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_attendance_status CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'HALF_DAY')),
    CONSTRAINT uq_attendance_date_student UNIQUE (attendance_date, student_id)
);

CREATE INDEX IF NOT EXISTS idx_attendance_student_date ON attendance_record (student_id, attendance_date DESC);

INSERT INTO announcement (title, message, target_role)
VALUES
    ('Welcome to BOSAMS', 'Use this portal to track classes, attendance, assignments, and school notices.', NULL),
    ('Teacher Workflow', 'Teachers should submit attendance before 10:00 AM daily.', 'TEACHER'),
    ('Parent Portal', 'Parents can monitor attendance and announcements from your dashboard.', 'PARENT')
ON CONFLICT DO NOTHING;
