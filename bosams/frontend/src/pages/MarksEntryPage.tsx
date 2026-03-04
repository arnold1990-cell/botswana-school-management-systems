import { useEffect, useMemo, useState } from 'react';
import api from '../api/client';
import { useAuth } from '../auth/AuthContext';

type AcademicYear = { year: number };
type Term = { id: number; termNo: number };
type Task = { id: number; type: 'CAT'|'EXAM' };
type Subject = { id: number; name: string };
type Learner = { id: number; admissionNo: string; firstName: string; lastName: string };
type MarkEntry = { learner: Learner; score: number };

const gradeOf = (score: number) => score >= 40 ? 'A' : score >= 35 ? 'B' : score >= 30 ? 'C' : score >= 25 ? 'D' : score >= 20 ? 'E' : 'F';

export const MarksEntryPage = () => {
  const { user } = useAuth();
  const [year, setYear] = useState<number>();
  const [terms, setTerms] = useState<Term[]>([]);
  const [termId, setTermId] = useState<number>();
  const [tasks, setTasks] = useState<Task[]>([]);
  const [taskId, setTaskId] = useState<number>();
  const [subjectId, setSubjectId] = useState<number>();
  const [gradeLevel, setGradeLevel] = useState(1);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [learners, setLearners] = useState<Learner[]>([]);
  const [scores, setScores] = useState<Record<number, number>>({});
  const [status, setStatus] = useState<'DRAFT' | 'SUBMITTED'>('DRAFT');
  const [testTypes, setTestTypes] = useState<string[]>([]);

  useEffect(() => { (async () => {
    const yr: AcademicYear = (await api.get('/academic-years/current')).data;
    setYear(yr.year);
    const [termRes, subRes, typeRes] = await Promise.all([
      api.get('/terms', { params: { year: yr.year } }),
      api.get('/subjects'),
      api.get('/test-types'),
    ]);
    setTerms(termRes.data); setTermId(termRes.data[0]?.id);
    setSubjects(subRes.data); setSubjectId(subRes.data[0]?.id);
    setTestTypes(typeRes.data);
    console.info('[marks] bootstrap', { year: yr.year, termCount: termRes.data.length, subjectCount: subRes.data.length, testTypes: typeRes.data });
  })(); }, []);

  useEffect(() => { if (!termId) return; api.get('/tasks', { params: { termId } }).then((r)=>{ setTasks(r.data); setTaskId(r.data[0]?.id); }); }, [termId]);
  useEffect(() => { api.get('/learners', { params: { gradeLevel } }).then((r)=>setLearners(r.data)); }, [gradeLevel]);
  useEffect(() => {
    if (!subjectId || !taskId) return;
    api.get('/marks/status', { params: { subjectId, taskId, gradeLevel } }).then((r) => setStatus(r.data.status));
    api.get('/marks', { params: { subjectId, taskId, gradeLevel } }).then((r) => {
      const entries = r.data as MarkEntry[];
      const prefill: Record<number, number> = {};
      entries.forEach((entry) => {
        prefill[entry.learner.id] = entry.score;
      });
      setScores(prefill);
      console.info('[marks] list', { subjectId, taskId, gradeLevel, rows: entries.length });
    });
  }, [subjectId, taskId, gradeLevel]);

  const rows = useMemo(()=>learners.map(l => ({...l, score: scores[l.id] ?? 0})), [learners, scores]);

  const save = async () => {
    if (!subjectId || !taskId) return;
    await api.post('/marks/bulk', { subjectId, taskId, marks: rows.map(r => ({ learnerId: r.id, score: Number(r.score) })) });
    alert('Marks saved successfully');
    setStatus('DRAFT');
  };

  const submit = async () => {
    if (!subjectId || !taskId) return;
    await api.post('/teacher/marks/submit', null, { params: { subjectId, taskId, gradeLevel } });
    setStatus('SUBMITTED');
  };

  const unlock = async () => {
    if (!subjectId || !taskId) return;
    await api.post('/admin/marks/unlock', null, { params: { subjectId, taskId, gradeLevel } });
    setStatus('DRAFT');
  };

  const locked = status === 'SUBMITTED';

  return <section>
    <h2>Marks Entry</h2>
    {locked && <p>Submitted/Locked</p>}
    <article className='card form-grid'>
      <label>Year <input value={year ?? ''} disabled /></label>
      <label>Term <select value={termId} onChange={(e)=>setTermId(Number(e.target.value))}>{terms.map(t => <option key={t.id} value={t.id}>Term {t.termNo}</option>)}</select></label>
      <label>Task <select value={taskId} onChange={(e)=>setTaskId(Number(e.target.value))}>{tasks.map(t => <option key={t.id} value={t.id}>{t.type}</option>)}</select></label>
      <label>Grade <select value={gradeLevel} onChange={(e)=>setGradeLevel(Number(e.target.value))}>{[1,2,3,4,5,6,7].map(g=><option key={g}>{g}</option>)}</select></label>
      <label>Subject <select value={subjectId} onChange={(e)=>setSubjectId(Number(e.target.value))}>{subjects.map(s=><option key={s.id} value={s.id}>{s.name}</option>)}</select></label>
      <label>Test Types <input value={testTypes.join(', ')} disabled /></label>
    </article>
    <article className='card'>
      <table className='table'><thead><tr><th>Learner</th><th>Mark /50</th><th>Grade</th></tr></thead>
        <tbody>{rows.map(r => <tr key={r.id}><td>{r.admissionNo} - {r.firstName} {r.lastName}</td><td><input disabled={locked} type='number' min={0} max={50} value={r.score} onChange={(e)=>setScores({...scores, [r.id]: Number(e.target.value)})} /></td><td>{gradeOf(Number(r.score))}</td></tr>)}</tbody>
      </table>
      {rows.length === 0 && <p>No learners found for this grade or your assignment scope.</p>}
      <button className='btn btn-primary' disabled={locked} onClick={save}>Save Draft</button>
      {user?.role === 'TEACHER' && <button className='btn btn-secondary' disabled={locked} onClick={submit}>Submit</button>}
      {(user?.role === 'ADMIN' || user?.role === 'PRINCIPAL') && locked && <button className='btn btn-secondary' onClick={unlock}>Unlock</button>}
    </article>
  </section>;
};
