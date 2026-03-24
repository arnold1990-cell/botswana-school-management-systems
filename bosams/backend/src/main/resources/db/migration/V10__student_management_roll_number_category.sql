ALTER TABLE student
    ADD COLUMN IF NOT EXISTS roll_number INT,
    ADD COLUMN IF NOT EXISTS student_category VARCHAR(80),
    ADD COLUMN IF NOT EXISTS guardian_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS guardian_phone VARCHAR(20),
    ADD COLUMN IF NOT EXISTS guardian_email VARCHAR(120);

CREATE UNIQUE INDEX IF NOT EXISTS uq_student_grade_roll
    ON student (grade_level, roll_number)
    WHERE roll_number IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_student_grade_roll
    ON student (grade_level, roll_number);

CREATE INDEX IF NOT EXISTS idx_student_category
    ON student (student_category);
