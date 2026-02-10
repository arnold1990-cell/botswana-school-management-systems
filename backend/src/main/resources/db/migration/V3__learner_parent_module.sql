CREATE TABLE learner_applications (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  application_no VARCHAR(40) NOT NULL,
  applied_date DATE NOT NULL,
  status VARCHAR(30) NOT NULL,
  preferred_grade_id BIGINT,
  preferred_academic_year_id BIGINT,
  learner_first_name VARCHAR(120) NOT NULL,
  learner_last_name VARCHAR(120) NOT NULL,
  date_of_birth DATE,
  gender VARCHAR(30),
  previous_school VARCHAR(160),
  notes TEXT,
  decision_note TEXT,
  decided_at TIMESTAMP,
  decided_by VARCHAR(120),
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_learner_application_no UNIQUE (school_id, application_no)
);

CREATE TABLE learners (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_no VARCHAR(40) NOT NULL,
  first_name VARCHAR(120) NOT NULL,
  last_name VARCHAR(120) NOT NULL,
  date_of_birth DATE,
  gender VARCHAR(30),
  national_id VARCHAR(40),
  home_language VARCHAR(80),
  status VARCHAR(30) NOT NULL,
  current_academic_year_id BIGINT,
  current_grade_id BIGINT,
  current_class_room_id BIGINT,
  house_id BIGINT,
  admission_date DATE,
  archived_at TIMESTAMP,
  archive_reason VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_learner_no UNIQUE (school_id, learner_no)
);

CREATE TABLE learner_transfers (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  transfer_type VARCHAR(40) NOT NULL,
  from_academic_year_id BIGINT,
  from_class_room_id BIGINT,
  from_grade_id BIGINT,
  to_academic_year_id BIGINT,
  to_class_room_id BIGINT,
  to_grade_id BIGINT,
  effective_date DATE NOT NULL,
  reason VARCHAR(255),
  captured_by VARCHAR(120),
  captured_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100)
);

CREATE TABLE parent_guardians (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  parent_no VARCHAR(40),
  first_name VARCHAR(120) NOT NULL,
  last_name VARCHAR(120) NOT NULL,
  national_id VARCHAR(40),
  email VARCHAR(120),
  phone VARCHAR(40),
  address VARCHAR(255),
  status VARCHAR(30) NOT NULL,
  archived_at TIMESTAMP,
  archive_reason VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_parent_no UNIQUE (school_id, parent_no)
);

CREATE TABLE learner_parent_links (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  parent_id BIGINT NOT NULL,
  relationship_type VARCHAR(30) NOT NULL,
  is_primary_contact BOOLEAN NOT NULL,
  lives_with_learner BOOLEAN NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_learner_parent_link UNIQUE (school_id, learner_id, parent_id)
);
CREATE UNIQUE INDEX uq_primary_parent_per_learner ON learner_parent_links (school_id, learner_id) WHERE is_primary_contact = true;

CREATE TABLE learner_activity_memberships (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  activity_type VARCHAR(30) NOT NULL,
  activity_id BIGINT NOT NULL,
  role VARCHAR(30) NOT NULL,
  start_date DATE,
  end_date DATE,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_learner_activity UNIQUE (school_id, learner_id, activity_type, activity_id)
);

CREATE TABLE learner_transport_assignments (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  bus_route_id BIGINT NOT NULL,
  ticket_type_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_transport_assign UNIQUE (school_id, learner_id, start_date)
);

CREATE TABLE learner_leadership_roles (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  role_type VARCHAR(30) NOT NULL,
  academic_year_id BIGINT NOT NULL,
  start_date DATE,
  end_date DATE,
  notes VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_leadership UNIQUE (school_id, learner_id, role_type, academic_year_id)
);

CREATE TABLE learner_incidents (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  incident_date DATE NOT NULL,
  category VARCHAR(30) NOT NULL,
  description TEXT NOT NULL,
  action_taken TEXT,
  reported_by VARCHAR(120) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100)
);

CREATE TABLE learner_learning_barriers (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  barrier_type VARCHAR(30) NOT NULL,
  notes TEXT,
  identified_date DATE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100)
);

CREATE TABLE learner_mentor_assignments (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  mentor_staff_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE,
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_mentor_assignment UNIQUE (school_id, learner_id, start_date)
);

CREATE TABLE learner_discipline_entries (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  entry_date DATE NOT NULL,
  entry_type VARCHAR(20) NOT NULL,
  code_id BIGINT NOT NULL,
  points INT NOT NULL,
  notes VARCHAR(255),
  captured_by VARCHAR(120),
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100)
);

CREATE TABLE learner_detention_actions (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  scheduled_date DATE NOT NULL,
  duration_minutes INT NOT NULL,
  reason VARCHAR(255),
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100)
);

CREATE TABLE learner_attendance_records (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  academic_year_id BIGINT,
  term_id BIGINT,
  date DATE NOT NULL,
  period INT,
  status VARCHAR(20) NOT NULL,
  notes VARCHAR(255),
  captured_by VARCHAR(120),
  captured_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_attendance UNIQUE (school_id, learner_id, date, period)
);

CREATE TABLE learner_absence_notifications (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL,
  learner_id BIGINT NOT NULL,
  date DATE NOT NULL,
  channel VARCHAR(20) NOT NULL,
  message TEXT NOT NULL,
  sent_at TIMESTAMP,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100)
);
