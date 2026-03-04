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
ON CONFLICT (name) DO UPDATE
SET code = EXCLUDED.code,
    status = 'ACTIVE';

INSERT INTO academic_year (label, start_date, end_date, is_active, year, status)
SELECT
    CAST(EXTRACT(YEAR FROM CURRENT_DATE) AS VARCHAR),
    make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INT, 1, 1),
    make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INT, 12, 31),
    TRUE,
    EXTRACT(YEAR FROM CURRENT_DATE)::INT,
    'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1
    FROM academic_year
    WHERE is_active = TRUE
);

WITH active_year AS (
    SELECT id, year
    FROM academic_year
    WHERE is_active = TRUE
    ORDER BY id DESC
    LIMIT 1
)
INSERT INTO term (academic_year_id, term_no, start_date, end_date)
SELECT ay.id,
       t.term_no,
       make_date(ay.year, ((t.term_no - 1) * 4) + 1, 1),
       (make_date(ay.year, ((t.term_no - 1) * 4) + 1, 1) + interval '4 month - 1 day')::date
FROM active_year ay
CROSS JOIN (VALUES (1), (2), (3)) AS t(term_no)
ON CONFLICT (academic_year_id, term_no) DO NOTHING;

INSERT INTO assessment_task (term_id, type, max_score)
SELECT t.id, tt.type, 50
FROM term t
JOIN academic_year ay ON ay.id = t.academic_year_id
CROSS JOIN (VALUES ('CAT'), ('EXAM')) AS tt(type)
WHERE ay.is_active = TRUE
ON CONFLICT (term_id, type) DO NOTHING;
