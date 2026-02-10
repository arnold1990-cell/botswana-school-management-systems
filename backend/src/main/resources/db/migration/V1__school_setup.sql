CREATE TABLE schools (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  address VARCHAR(255),
  contact_email VARCHAR(100),
  contact_phone VARCHAR(40),
  logo_url VARCHAR(255),
  document_header VARCHAR(255),
  report_languages VARCHAR(100),
  default_term_structure VARCHAR(100),
  document_number_format VARCHAR(100),
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100)
);

CREATE TABLE academic_years (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL REFERENCES schools(id),
  name VARCHAR(100) NOT NULL,
  start_date DATE,
  end_date DATE,
  active BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_academic_year UNIQUE(school_id,name)
);
CREATE TABLE terms (
  id BIGSERIAL PRIMARY KEY,
  school_id BIGINT NOT NULL REFERENCES schools(id),
  academic_year_id BIGINT NOT NULL REFERENCES academic_years(id),
  name VARCHAR(100) NOT NULL,
  start_date DATE,
  end_date DATE,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(100),
  updated_by VARCHAR(100),
  CONSTRAINT uq_term UNIQUE(academic_year_id,name)
);
CREATE TABLE grades (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), name VARCHAR(80) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100), CONSTRAINT uq_grade UNIQUE(school_id,name));
CREATE TABLE classes (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), academic_year_id BIGINT NOT NULL REFERENCES academic_years(id), grade_id BIGINT NOT NULL REFERENCES grades(id), code VARCHAR(30) NOT NULL, name VARCHAR(100) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100), CONSTRAINT uq_class_code UNIQUE(school_id,academic_year_id,code));
CREATE TABLE houses (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), name VARCHAR(100) NOT NULL,
created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100), CONSTRAINT uq_house UNIQUE(school_id,name));
CREATE TABLE extra_mural_types (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), name VARCHAR(100) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100));
CREATE TABLE extra_mural_activities (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), type_id BIGINT NOT NULL REFERENCES extra_mural_types(id), name VARCHAR(100) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100));
CREATE TABLE sports (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), name VARCHAR(100) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100));
CREATE TABLE teams (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), sport_id BIGINT NOT NULL REFERENCES sports(id), name VARCHAR(100) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100));
CREATE TABLE competitions (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), sport_id BIGINT NOT NULL REFERENCES sports(id), name VARCHAR(100) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100));
CREATE TABLE bus_routes (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), name VARCHAR(120) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100));
CREATE TABLE bus_ticket_types (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), name VARCHAR(120) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100));
CREATE TABLE merit_codes (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), code VARCHAR(40) NOT NULL, description VARCHAR(120) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100), CONSTRAINT uq_merit_code UNIQUE(school_id,code));
CREATE TABLE demerit_codes (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), code VARCHAR(40) NOT NULL, description VARCHAR(120) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100), CONSTRAINT uq_demerit_code UNIQUE(school_id,code));
CREATE TABLE hostels (id BIGSERIAL PRIMARY KEY, school_id BIGINT NOT NULL REFERENCES schools(id), name VARCHAR(100) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now(), created_by VARCHAR(100), updated_by VARCHAR(100));

INSERT INTO schools(id,name,address,contact_email,contact_phone,document_header,report_languages,default_term_structure,document_number_format)
VALUES (1,'Demo Senior Secondary','Gaborone','admin@demo.bw','+267000000','Demo School Header','EN,TSW','3_TERMS','DOC-{yyyy}-{seq}');
INSERT INTO academic_years(id,school_id,name,active) VALUES (1,1,'2025',true);
INSERT INTO terms(school_id,academic_year_id,name) VALUES (1,1,'Term 1'),(1,1,'Term 2'),(1,1,'Term 3');
INSERT INTO houses(school_id,name) VALUES (1,'Kgale'),(1,'Tsodilo');
INSERT INTO merit_codes(school_id,code,description) VALUES (1,'MERIT-ATT','Perfect Attendance');
INSERT INTO demerit_codes(school_id,code,description) VALUES (1,'DEMERIT-LATE','Late Coming');
SELECT setval('schools_id_seq', (SELECT max(id) FROM schools));
SELECT setval('academic_years_id_seq', (SELECT max(id) FROM academic_years));
