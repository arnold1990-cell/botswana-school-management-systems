ALTER TABLE teacher_assignment ADD COLUMN IF NOT EXISTS grade_level INT;
UPDATE teacher_assignment ta
SET grade_level = COALESCE(ta.grade_level, NULLIF(regexp_replace(st.name, '\\D', '', 'g'), '')::INT)
FROM stream s
JOIN standard st ON st.id = s.standard_id
WHERE ta.stream_id = s.id;
UPDATE teacher_assignment SET grade_level = COALESCE(grade_level, 1);
ALTER TABLE teacher_assignment ALTER COLUMN grade_level SET NOT NULL;
ALTER TABLE teacher_assignment ALTER COLUMN stream_id DROP NOT NULL;

ALTER TABLE mark_entry ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'DRAFT';
ALTER TABLE mark_entry ADD COLUMN IF NOT EXISTS submitted_at TIMESTAMPTZ;
ALTER TABLE mark_entry ADD COLUMN IF NOT EXISTS submitted_by_user_id UUID REFERENCES users (id);
UPDATE mark_entry SET status = 'DRAFT' WHERE status IS NULL;
