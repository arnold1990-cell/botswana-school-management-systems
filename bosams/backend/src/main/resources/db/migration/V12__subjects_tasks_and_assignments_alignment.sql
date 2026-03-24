ALTER TABLE subject
    DROP CONSTRAINT IF EXISTS subject_name_key;

ALTER TABLE subject
    ALTER COLUMN code SET NOT NULL;

ALTER TABLE subject
    ADD COLUMN IF NOT EXISTS school_level VARCHAR(30) NOT NULL DEFAULT 'PRIMARY',
    ADD COLUMN IF NOT EXISTS grade_from INT NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS grade_to INT NOT NULL DEFAULT 7,
    ADD COLUMN IF NOT EXISTS is_elective BOOLEAN NOT NULL DEFAULT FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS uq_subject_code ON subject(code);
CREATE UNIQUE INDEX IF NOT EXISTS uq_subject_level_range_code ON subject(school_level, grade_from, grade_to, code);

UPDATE subject
SET school_level = 'PRIMARY',
    grade_from = 1,
    grade_to = 7
WHERE school_level IS NULL;

ALTER TABLE assessment_task
    DROP CONSTRAINT IF EXISTS uq_assessment_task_term_type;

ALTER TABLE assessment_task
    DROP CONSTRAINT IF EXISTS chk_assessment_task_type;

ALTER TABLE assessment_task
    ADD COLUMN IF NOT EXISTS subject_id BIGINT REFERENCES subject(id),
    ADD COLUMN IF NOT EXISTS grade_level INT,
    ADD COLUMN IF NOT EXISTS title VARCHAR(200),
    ADD COLUMN IF NOT EXISTS description_text TEXT,
    ADD COLUMN IF NOT EXISTS due_date DATE,
    ADD COLUMN IF NOT EXISTS created_by_user_id UUID REFERENCES users(id);

UPDATE assessment_task
SET title = CASE WHEN type = 'CAT' THEN 'Continuous Assessment Test' ELSE 'End-of-term Exam' END
WHERE title IS NULL;

ALTER TABLE assessment_task
    ALTER COLUMN title SET NOT NULL;

ALTER TABLE assessment_task
    ADD CONSTRAINT chk_assessment_task_type CHECK (type IN ('EXERCISE', 'QUIZ', 'TEST', 'PROJECT', 'MID_TERM', 'END_OF_TERM_EXAM', 'ASSIGNMENT', 'CAT', 'EXAM'));

CREATE INDEX IF NOT EXISTS idx_assessment_task_filter ON assessment_task(term_id, grade_level, subject_id, type);

INSERT INTO subject (name, code, school_level, grade_from, grade_to, is_elective, status)
VALUES
    ('English', 'PRIMARY_ENGLISH', 'PRIMARY', 1, 4, FALSE, 'ACTIVE'),
    ('Setswana', 'PRIMARY_SETSWANA', 'PRIMARY', 1, 4, FALSE, 'ACTIVE'),
    ('Mathematics', 'PRIMARY_MATHEMATICS', 'PRIMARY', 1, 4, FALSE, 'ACTIVE'),
    ('Environmental Science', 'PRIMARY_ENVIRONMENTAL_SCIENCE', 'PRIMARY', 1, 4, FALSE, 'ACTIVE'),
    ('Social Studies', 'PRIMARY_SOCIAL_STUDIES', 'PRIMARY', 1, 4, FALSE, 'ACTIVE'),
    ('Religious & Moral Education', 'PRIMARY_RME', 'PRIMARY', 1, 4, FALSE, 'ACTIVE'),
    ('Agriculture', 'PRIMARY_AGRICULTURE', 'PRIMARY', 1, 4, FALSE, 'ACTIVE'),
    ('Creative and Performing Arts', 'PRIMARY_CAPA', 'PRIMARY', 1, 4, FALSE, 'ACTIVE'),
    ('Physical Education', 'PRIMARY_PHYSICAL_EDUCATION', 'PRIMARY', 1, 4, FALSE, 'ACTIVE'),

    ('English', 'UPPER_PRIMARY_ENGLISH', 'PRIMARY', 5, 7, FALSE, 'ACTIVE'),
    ('Setswana', 'UPPER_PRIMARY_SETSWANA', 'PRIMARY', 5, 7, FALSE, 'ACTIVE'),
    ('Mathematics', 'UPPER_PRIMARY_MATHEMATICS', 'PRIMARY', 5, 7, FALSE, 'ACTIVE'),
    ('Science', 'UPPER_PRIMARY_SCIENCE', 'PRIMARY', 5, 7, FALSE, 'ACTIVE'),
    ('Social Studies', 'UPPER_PRIMARY_SOCIAL_STUDIES', 'PRIMARY', 5, 7, FALSE, 'ACTIVE'),
    ('Agriculture', 'UPPER_PRIMARY_AGRICULTURE', 'PRIMARY', 5, 7, FALSE, 'ACTIVE'),
    ('Religious & Moral Education', 'UPPER_PRIMARY_RME', 'PRIMARY', 5, 7, FALSE, 'ACTIVE'),
    ('Creative and Performing Arts', 'UPPER_PRIMARY_CAPA', 'PRIMARY', 5, 7, FALSE, 'ACTIVE'),
    ('Physical Education', 'UPPER_PRIMARY_PHYSICAL_EDUCATION', 'PRIMARY', 5, 7, FALSE, 'ACTIVE'),
    ('Computer Awareness / ICT', 'UPPER_PRIMARY_ICT', 'PRIMARY', 5, 7, TRUE, 'ACTIVE'),

    ('English', 'JUNIOR_ENGLISH', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('Setswana', 'JUNIOR_SETSWANA', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('Mathematics', 'JUNIOR_MATHEMATICS', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('Integrated Science', 'JUNIOR_INTEGRATED_SCIENCE', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('Agriculture', 'JUNIOR_AGRICULTURE', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('Moral Education', 'JUNIOR_MORAL_EDUCATION', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('Religious Education', 'JUNIOR_RELIGIOUS_EDUCATION', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('Social Studies', 'JUNIOR_SOCIAL_STUDIES', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('Geography', 'JUNIOR_GEOGRAPHY', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('History', 'JUNIOR_HISTORY', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('Design & Technology', 'JUNIOR_DESIGN_TECHNOLOGY', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('Art', 'JUNIOR_ART', 'JUNIOR_SECONDARY', 8, 10, TRUE, 'ACTIVE'),
    ('Music', 'JUNIOR_MUSIC', 'JUNIOR_SECONDARY', 8, 10, TRUE, 'ACTIVE'),
    ('Physical Education', 'JUNIOR_PHYSICAL_EDUCATION', 'JUNIOR_SECONDARY', 8, 10, FALSE, 'ACTIVE'),
    ('Home Economics', 'JUNIOR_HOME_ECONOMICS', 'JUNIOR_SECONDARY', 8, 10, TRUE, 'ACTIVE'),
    ('Commerce', 'JUNIOR_COMMERCE', 'JUNIOR_SECONDARY', 8, 10, TRUE, 'ACTIVE'),
    ('Office Procedures', 'JUNIOR_OFFICE_PROCEDURES', 'JUNIOR_SECONDARY', 8, 10, TRUE, 'ACTIVE'),
    ('Accounting Basics', 'JUNIOR_ACCOUNTING_BASICS', 'JUNIOR_SECONDARY', 8, 10, TRUE, 'ACTIVE'),
    ('French', 'JUNIOR_FRENCH', 'JUNIOR_SECONDARY', 8, 10, TRUE, 'ACTIVE'),

    ('English Language', 'SENIOR_ENGLISH_LANGUAGE', 'SENIOR_SECONDARY', 11, 12, FALSE, 'ACTIVE'),
    ('Setswana', 'SENIOR_SETSWANA', 'SENIOR_SECONDARY', 11, 12, FALSE, 'ACTIVE'),
    ('Mathematics', 'SENIOR_MATHEMATICS', 'SENIOR_SECONDARY', 11, 12, FALSE, 'ACTIVE'),
    ('Additional Mathematics', 'SENIOR_ADDITIONAL_MATHEMATICS', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Physics', 'SENIOR_PHYSICS', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Chemistry', 'SENIOR_CHEMISTRY', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Biology', 'SENIOR_BIOLOGY', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Science Double Award', 'SENIOR_SCIENCE_DOUBLE_AWARD', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Geography', 'SENIOR_GEOGRAPHY', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('History', 'SENIOR_HISTORY', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Social Studies', 'SENIOR_SOCIAL_STUDIES', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Development Studies', 'SENIOR_DEVELOPMENT_STUDIES', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Religious Education', 'SENIOR_RELIGIOUS_EDUCATION', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Literature in English', 'SENIOR_LITERATURE_IN_ENGLISH', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Agriculture', 'SENIOR_AGRICULTURE', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Accounting', 'SENIOR_ACCOUNTING', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Business Studies', 'SENIOR_BUSINESS_STUDIES', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Commerce', 'SENIOR_COMMERCE', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Computer Studies', 'SENIOR_COMPUTER_STUDIES', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Design & Technology', 'SENIOR_DESIGN_TECHNOLOGY', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Food & Nutrition', 'SENIOR_FOOD_NUTRITION', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Fashion & Fabrics', 'SENIOR_FASHION_FABRICS', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Home Management', 'SENIOR_HOME_MANAGEMENT', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Music', 'SENIOR_MUSIC', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Physical Education', 'SENIOR_PHYSICAL_EDUCATION', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE'),
    ('Art', 'SENIOR_ART', 'SENIOR_SECONDARY', 11, 12, TRUE, 'ACTIVE')
ON CONFLICT (code) DO UPDATE
SET name = EXCLUDED.name,
    school_level = EXCLUDED.school_level,
    grade_from = EXCLUDED.grade_from,
    grade_to = EXCLUDED.grade_to,
    is_elective = EXCLUDED.is_elective,
    status = 'ACTIVE';
