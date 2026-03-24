import { FormEvent, useEffect, useState } from 'react';
import api from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { useAuthReady } from '../auth/useAuthReady';

type Teacher = { id: string; fullName: string; email: string };
type Subject = { id: number; name: string };
type AcademicYear = { id: number; year: number };

type Assignment = { id: number; teacher: Teacher; gradeLevel: number; subject: Subject; academicYear: AcademicYear };

export const TeachersPage = () => {
  const { user } = useAuth();
  const { authReady, authLoading } = useAuthReady();
  const [teachers, setTeachers] = useState<Teacher[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [yearId, setYearId] = useState<number>();
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [teacherForm, setTeacherForm] = useState({ fullName: '', email: '', password: '' });
  const [assignmentForm, setAssignmentForm] = useState({ teacherUserId: '', gradeLevel: 1, subjectId: 0 });

  const load = async () => {
    const [teacherRes, subjectRes, yearRes, assignmentRes] = await Promise.all([
      api.get('/admin/teachers'),
      api.get('/subjects'),
      api.get('/academic-years/current'),
      api.get('/admin/teacher-assignments'),
    ]);
    setTeachers(teacherRes.data);
    setSubjects(subjectRes.data);
    setYearId(yearRes.data.id);
    setAssignments(assignmentRes.data);
    setAssignmentForm((prev) => ({ ...prev, subjectId: subjectRes.data[0]?.id ?? 0, teacherUserId: teacherRes.data[0]?.id ?? '' }));
  };

  useEffect(() => {
    if (!authReady) return;
    load();
  }, [authReady]);

  const createTeacher = async (event: FormEvent) => {
    event.preventDefault();
    await api.post('/admin/teachers', teacherForm);
    setTeacherForm({ fullName: '', email: '', password: '' });
    await load();
  };

  const assignTeacher = async (event: FormEvent) => {
    event.preventDefault();
    if (!yearId) return;
    await api.post('/admin/teacher-assignments', { ...assignmentForm, academicYearId: yearId, subjectId: Number(assignmentForm.subjectId) });
    await load();
  };

  const canManageTeachers = user?.role === 'ADMIN';

  return <section>
    <h2>Teacher Management</h2>
    {authLoading && <p className='muted'>Loading authentication…</p>}
    {canManageTeachers && <form className='card form-grid' onSubmit={createTeacher}>
      <h3>Create Teacher</h3>
      <input value={teacherForm.fullName} onChange={(e)=>setTeacherForm({...teacherForm, fullName: e.target.value})} placeholder='Full name' required />
      <input type='email' value={teacherForm.email} onChange={(e)=>setTeacherForm({...teacherForm, email: e.target.value})} placeholder='Email' required />
      <input type='password' value={teacherForm.password} onChange={(e)=>setTeacherForm({...teacherForm, password: e.target.value})} placeholder='Password' required />
      <button className='btn btn-primary' type='submit'>Create Teacher</button>
    </form>}

    {canManageTeachers && <form className='card form-grid' onSubmit={assignTeacher}>
      <h3>Assign Teacher</h3>
      <select value={assignmentForm.teacherUserId} onChange={(e)=>setAssignmentForm({...assignmentForm, teacherUserId: e.target.value})}>{teachers.map(t=><option key={t.id} value={t.id}>{t.fullName}</option>)}</select>
      <select value={assignmentForm.gradeLevel} onChange={(e)=>setAssignmentForm({...assignmentForm, gradeLevel: Number(e.target.value)})}>{[1,2,3,4,5,6,7].map(g=><option key={g} value={g}>Grade {g}</option>)}</select>
      <select value={assignmentForm.subjectId} onChange={(e)=>setAssignmentForm({...assignmentForm, subjectId: Number(e.target.value)})}>{subjects.map(s=><option key={s.id} value={s.id}>{s.name}</option>)}</select>
      <button className='btn btn-primary' type='submit'>Assign</button>
    </form>}

    {!canManageTeachers && <p>Principals can view teacher assignments but cannot create teachers or assign teachers.</p>}

    <article className='card'>
      <table className='table'>
        <thead><tr><th>Teacher</th><th>Grade</th><th>Subject</th><th>Year</th></tr></thead>
        <tbody>{assignments.map((row) => <tr key={row.id}><td>{row.teacher.fullName}</td><td>{row.gradeLevel}</td><td>{row.subject.name}</td><td>{row.academicYear.year}</td></tr>)}</tbody>
      </table>
    </article>
  </section>;
};
