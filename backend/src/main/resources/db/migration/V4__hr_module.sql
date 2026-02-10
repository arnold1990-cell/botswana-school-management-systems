CREATE TABLE hr_staff_members (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL REFERENCES schools(id),
  staff_number VARCHAR(40) NOT NULL,
  staff_type VARCHAR(30) NOT NULL,
  title VARCHAR(20),
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  national_id VARCHAR(50),
  gender VARCHAR(20),
  date_of_birth DATE,
  employment_start_date DATE,
  employment_end_date DATE,
  status VARCHAR(20) NOT NULL,
  email VARCHAR(120),
  phone VARCHAR(40),
  address_line1 VARCHAR(120),
  address_line2 VARCHAR(120),
  city VARCHAR(80),
  district VARCHAR(80),
  postal_code VARCHAR(20),
  archived_at TIMESTAMP,
  archived_reason TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_hr_staff_number UNIQUE (school_id, staff_number)
);

CREATE TABLE hr_educator_subject_experience (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL REFERENCES schools(id),
  staff_id BIGINT NOT NULL REFERENCES hr_staff_members(id),
  subject_id BIGINT NOT NULL,
  years_experience INTEGER NOT NULL,
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_hr_subject_experience UNIQUE (school_id, staff_id, subject_id)
);

CREATE TABLE hr_register_class_assignments (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL REFERENCES schools(id),
  staff_id BIGINT NOT NULL REFERENCES hr_staff_members(id),
  class_room_id BIGINT NOT NULL,
  academic_year_id BIGINT NOT NULL,
  role VARCHAR(30) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_hr_register_class_role UNIQUE (school_id, academic_year_id, class_room_id, role)
);

CREATE TABLE hr_staff_leave_requests (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL REFERENCES schools(id),
  staff_id BIGINT NOT NULL REFERENCES hr_staff_members(id),
  leave_type VARCHAR(30) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  reason TEXT,
  status VARCHAR(20) NOT NULL,
  decided_by VARCHAR(100),
  decided_at TIMESTAMP,
  decision_note TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100)
);

CREATE TABLE hr_staff_attendance_records (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL REFERENCES schools(id),
  staff_id BIGINT NOT NULL REFERENCES hr_staff_members(id),
  date DATE NOT NULL,
  status VARCHAR(30) NOT NULL,
  notes TEXT,
  captured_by VARCHAR(100),
  captured_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_hr_staff_attendance UNIQUE (school_id, staff_id, date)
);

CREATE TABLE hr_training_programs (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL REFERENCES schools(id),
  title VARCHAR(200) NOT NULL,
  provider VARCHAR(150),
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  category VARCHAR(30) NOT NULL,
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100)
);

CREATE TABLE hr_training_attendance (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL REFERENCES schools(id),
  training_program_id BIGINT NOT NULL REFERENCES hr_training_programs(id),
  staff_id BIGINT NOT NULL REFERENCES hr_staff_members(id),
  attended BOOLEAN NOT NULL,
  certificate_url VARCHAR(500),
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_hr_training_staff UNIQUE (school_id, training_program_id, staff_id)
);

CREATE TABLE hr_appraisal_records (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL REFERENCES schools(id),
  staff_id BIGINT NOT NULL REFERENCES hr_staff_members(id),
  appraisal_date DATE NOT NULL,
  appraisal_type VARCHAR(30) NOT NULL,
  reviewer_name VARCHAR(120) NOT NULL,
  score INTEGER,
  summary TEXT,
  recommendations TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100)
);

CREATE INDEX idx_hr_staff_school ON hr_staff_members(school_id);
CREATE INDEX idx_hr_leave_school_status ON hr_staff_leave_requests(school_id, status);
CREATE INDEX idx_hr_attendance_school_date ON hr_staff_attendance_records(school_id, date);
CREATE INDEX idx_hr_register_school_year_class ON hr_register_class_assignments(school_id, academic_year_id, class_room_id);
