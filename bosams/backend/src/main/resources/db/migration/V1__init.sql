create table users (
 id bigserial primary key,
 full_name varchar(255) not null,
 email varchar(255) unique not null,
 password_hash varchar(255) not null,
 role varchar(20) not null,
 status varchar(20) not null,
 created_at timestamp,
 updated_at timestamp
);
create table standard (id bigserial primary key, name varchar(100) unique not null);
create table stream (id bigserial primary key, standard_id bigint not null references standard(id), name varchar(120) not null);
create table subject (id bigserial primary key, name varchar(120) unique not null);
create table student (id bigserial primary key, admission_no varchar(50) unique not null, first_name varchar(120) not null, last_name varchar(120) not null, gender varchar(20), stream_id bigint not null references stream(id), status varchar(20));
create table academic_year (id bigserial primary key, label varchar(20) unique not null, start_date date not null, end_date date not null, is_active boolean not null default false);
create table term (id bigserial primary key, academic_year_id bigint not null references academic_year(id), term_no int not null, start_date date not null, end_date date not null);
create table exam_group (id bigserial primary key, academic_year_id bigint not null references academic_year(id), term_id bigint references term(id), name varchar(120) not null, status varchar(20) not null);
create table exam_schedule (id bigserial primary key, exam_group_id bigint not null references exam_group(id), stream_id bigint not null references stream(id), subject_id bigint not null references subject(id), exam_datetime timestamp not null, max_marks numeric(5,2) not null, mark_entry_last_date date not null, status varchar(20) not null);
create table mark (id bigserial primary key, exam_schedule_id bigint not null references exam_schedule(id), student_id bigint not null references student(id), marks numeric(5,2), absent boolean not null default false, grade varchar(5), result varchar(20), locked boolean not null default false, locked_at timestamp, unique(exam_schedule_id, student_id));
create table grade_profile (id bigserial primary key, name varchar(120) not null, rules_json text not null, is_default boolean not null default false);
create table teacher_profile (id bigserial primary key, user_id bigint unique not null references users(id), staff_no varchar(100) unique, phone varchar(100));
create table teacher_assignment (id bigserial primary key, teacher_user_id bigint not null references users(id), academic_year_id bigint not null references academic_year(id), stream_id bigint not null references stream(id), subject_id bigint not null references subject(id), active boolean not null default true, unique(teacher_user_id, academic_year_id, stream_id, subject_id));
create table audit_log (id bigserial primary key, actor_user_id bigint, action varchar(100) not null, entity varchar(100) not null, entity_id varchar(100), before_json text, after_json text, note text, created_at timestamp not null default now());
create table refresh_token (id bigserial primary key, user_id bigint not null references users(id), token varchar(500) unique not null, expires_at timestamp not null, revoked boolean not null default false);
create index idx_student_admission_no on student(admission_no);
create index idx_mark_schedule_student on mark(exam_schedule_id, student_id);
create index idx_teacher_assignment_teacher_year on teacher_assignment(teacher_user_id, academic_year_id);
create index idx_exam_schedule_group_stream_subject on exam_schedule(exam_group_id, stream_id, subject_id);
