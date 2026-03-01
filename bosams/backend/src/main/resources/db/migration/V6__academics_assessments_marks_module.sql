ALTER TABLE student ADD COLUMN IF NOT EXISTS grade_level INT;
ALTER TABLE student ALTER COLUMN stream_id DROP NOT NULL;
ALTER TABLE student ALTER COLUMN status SET DEFAULT 'ACTIVE';
ALTER TABLE student ADD CONSTRAINT chk_student_grade_level CHECK (grade_level BETWEEN 1 AND 7);

ALTER TABLE subject ADD COLUMN IF NOT EXISTS code VARCHAR(30);
ALTER TABLE subject ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE academic_year ADD COLUMN IF NOT EXISTS year INT;
ALTER TABLE academic_year ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE term ADD CONSTRAINT chk_term_number CHECK (term_no BETWEEN 1 AND 3);
ALTER TABLE term ADD CONSTRAINT uq_term_year_term UNIQUE (academic_year_id, term_no);

CREATE TABLE IF NOT EXISTS assessment_task (
    id BIGSERIAL PRIMARY KEY,
    term_id BIGINT NOT NULL REFERENCES term (id) ON DELETE CASCADE,
    type VARCHAR(10) NOT NULL,
    max_score INT NOT NULL DEFAULT 50,
    CONSTRAINT chk_assessment_task_type CHECK (type IN ('CAT', 'EXAM')),
    CONSTRAINT chk_assessment_task_max_score CHECK (max_score = 50),
    CONSTRAINT uq_assessment_task_term_type UNIQUE (term_id, type)
);

CREATE TABLE IF NOT EXISTS mark_entry (
    id BIGSERIAL PRIMARY KEY,
    learner_id BIGINT NOT NULL REFERENCES student (id) ON DELETE CASCADE,
    subject_id BIGINT NOT NULL REFERENCES subject (id),
    task_id BIGINT NOT NULL REFERENCES assessment_task (id) ON DELETE CASCADE,
    score INT NOT NULL,
    grade_letter VARCHAR(2) NOT NULL,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    recorded_by_user_id UUID REFERENCES users (id),
    CONSTRAINT chk_mark_entry_score CHECK (score BETWEEN 0 AND 50),
    CONSTRAINT chk_mark_entry_grade CHECK (grade_letter IN ('A', 'B', 'C', 'D', 'E', 'F')),
    CONSTRAINT uq_mark_entry_unique UNIQUE (learner_id, subject_id, task_id)
);

CREATE INDEX IF NOT EXISTS idx_student_grade_level ON student (grade_level);
CREATE INDEX IF NOT EXISTS idx_mark_entry_task_subject ON mark_entry (task_id, subject_id);

INSERT INTO subject (name, code, status)
VALUES
    ('Setswana', 'SETSWANA', 'ACTIVE'),
    ('English', 'ENGLISH', 'ACTIVE'),
    ('Mathematics', 'MATHEMATICS', 'ACTIVE'),
    ('Science', 'SCIENCE', 'ACTIVE'),
    ('CAPA', 'CAPA', 'ACTIVE'),
    ('RME', 'RME', 'ACTIVE'),
    ('Social Studies', 'SOCIAL_STUDIES', 'ACTIVE'),
    ('Agriculture', 'AGRICULTURE', 'ACTIVE')
ON CONFLICT (name) DO UPDATE SET
    code = EXCLUDED.code,
    status = 'ACTIVE';

INSERT INTO academic_year (label, start_date, end_date, is_active, year, status)
SELECT
    CAST(EXTRACT(YEAR FROM CURRENT_DATE) AS VARCHAR),
    make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INT, 1, 1),
    make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INT, 12, 31),
    TRUE,
    EXTRACT(YEAR FROM CURRENT_DATE)::INT,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM academic_year WHERE year = EXTRACT(YEAR FROM CURRENT_DATE)::INT);

UPDATE academic_year
SET year = label::INT
WHERE year IS NULL AND label ~ '^[0-9]{4}$';

WITH current_year AS (
    SELECT id FROM academic_year WHERE year = EXTRACT(YEAR FROM CURRENT_DATE)::INT ORDER BY id DESC LIMIT 1
)
INSERT INTO term (academic_year_id, term_no, start_date, end_date)
SELECT cy.id, t.term_no,
       CASE t.term_no WHEN 1 THEN make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INT,1,10)
                      WHEN 2 THEN make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INT,5,10)
                      ELSE make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INT,9,10) END,
       CASE t.term_no WHEN 1 THEN make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INT,4,20)
                      WHEN 2 THEN make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INT,8,20)
                      ELSE make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INT,12,10) END
FROM current_year cy
CROSS JOIN (VALUES (1),(2),(3)) AS t(term_no)
ON CONFLICT (academic_year_id, term_no) DO NOTHING;

INSERT INTO assessment_task (term_id, type, max_score)
SELECT tm.id, tt.type, 50
FROM term tm
JOIN academic_year ay ON ay.id = tm.academic_year_id
CROSS JOIN (VALUES ('CAT'), ('EXAM')) AS tt(type)
WHERE ay.year = EXTRACT(YEAR FROM CURRENT_DATE)::INT
ON CONFLICT (term_id, type) DO NOTHING;
