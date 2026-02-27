insert into users(full_name,email,password_hash,role,status,created_at,updated_at) values
('System Admin','admin@bosams.local','$2a$10$7QJQ4V7iY4S1v6j8TDIrM.t8H4J7Qx5QhD6sM5Y1fELm5Vx4jJYbS','ADMIN','ACTIVE',now(),now()),
('School Principal','principal@bosams.local','$2a$10$7QJQ4V7iY4S1v6j8TDIrM.t8H4J7Qx5QhD6sM5Y1fELm5Vx4jJYbS','PRINCIPAL','ACTIVE',now(),now()),
('Teacher Demo','teacher@bosams.local','$2a$10$7QJQ4V7iY4S1v6j8TDIrM.t8H4J7Qx5QhD6sM5Y1fELm5Vx4jJYbS','TEACHER','ACTIVE',now(),now());
insert into standard(name) values ('Standard 5');
insert into stream(standard_id,name) values (1,'2026 G');
insert into subject(name) values ('Mathematics'),('English');
insert into student(admission_no,first_name,last_name,gender,stream_id,status) values ('ADM001','Neo','Kabelo','MALE',1,'ACTIVE'),('ADM002','Amina','Dube','FEMALE',1,'ACTIVE');
insert into academic_year(label,start_date,end_date,is_active) values ('2026','2026-01-01','2026-12-31',true),('2025','2025-01-01','2025-12-31',false);
insert into term(academic_year_id,term_no,start_date,end_date) values (1,1,'2026-01-10','2026-04-10');
insert into exam_group(academic_year_id,term_id,name,status) values (1,1,'Term 1 CAT','ACTIVE');
insert into exam_schedule(exam_group_id,stream_id,subject_id,exam_datetime,max_marks,mark_entry_last_date,status) values (1,1,1,now(),100,'2030-12-31','ACTIVE');
insert into grade_profile(name,rules_json,is_default) values ('Default','{"boundaries":[{"grade":"A","min":80},{"grade":"B","min":70},{"grade":"C","min":60},{"grade":"D","min":50},{"grade":"F","min":0}],"passThreshold":50}',true);
insert into teacher_profile(user_id,staff_no,phone) values (3,'T-001','+26770000000');
insert into teacher_assignment(teacher_user_id,academic_year_id,stream_id,subject_id,active) values (3,1,1,1,true);
