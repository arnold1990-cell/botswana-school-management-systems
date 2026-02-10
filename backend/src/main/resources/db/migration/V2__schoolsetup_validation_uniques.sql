ALTER TABLE grades DROP CONSTRAINT IF EXISTS uq_grade;
ALTER TABLE classes DROP CONSTRAINT IF EXISTS uq_class_code;
ALTER TABLE houses DROP CONSTRAINT IF EXISTS uq_house;
ALTER TABLE merit_codes DROP CONSTRAINT IF EXISTS uq_merit_code;
ALTER TABLE demerit_codes DROP CONSTRAINT IF EXISTS uq_demerit_code;

ALTER TABLE grades ADD CONSTRAINT uq_grade_school_name UNIQUE (school_id, name);
ALTER TABLE classes ADD CONSTRAINT uq_class_school_year_code UNIQUE (school_id, academic_year_id, code);
ALTER TABLE houses ADD CONSTRAINT uq_house_school_name UNIQUE (school_id, name);
ALTER TABLE merit_codes ADD CONSTRAINT uq_merit_code_school_code UNIQUE (school_id, code);
ALTER TABLE demerit_codes ADD CONSTRAINT uq_demerit_code_school_code UNIQUE (school_id, code);
