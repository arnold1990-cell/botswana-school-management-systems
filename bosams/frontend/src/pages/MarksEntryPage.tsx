import { AxiosError } from 'axios';
import { useEffect, useMemo, useState } from 'react';
import api from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { useAuthReady } from '../auth/useAuthReady';

type AcademicYear = { year: number };
type Term = { id: number; termNo: number };
type Task = { id: number; type: string; title: string };
type Subject = { id: number; name: string };
type Learner = { id: number; admissionNo: string; firstName: string; lastName: string };
type MarkEntry = { learner: Learner; score: number };

const gradeOf = (score: number) => score >= 40 ? 'A' : score >= 35 ? 'B' : score >= 30 ? 'C' : score >= 25 ? 'D' : score >= 20 ? 'E' : 'F';

const toMessage = (error: unknown, fallback: string) => {
  const axiosError = error as AxiosError<{ message?: string }>;
  const status = axiosError.response?.status;
  if (status === 401) {
    return 'Authentication required. Please sign in again.';
  }
  if (status === 403) {
    return 'Access denied. You do not have permission to load marks setup data.';
  }
  return axiosError.response?.data?.message ?? fallback;
};

export const MarksEntryPage = () => {
  const { user } = useAuth();
  const { authReady, authLoading, isAuthenticated } = useAuthReady();
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
  const [loadError, setLoadError] = useState<string>('');
  const [saveMessage, setSaveMessage] = useState<string>('');


  useEffect(() => { (async () => {
    if (!authReady || !isAuthenticated) return;
    try {
      setLoadError('');
      const yr: AcademicYear = (await api.get('/academics/active-year')).data;
      setYear(yr.year);

      const [termRes, typeRes] = await Promise.all([
        api.get('/terms', { params: { year: yr.year } }),
        api.get('/test-types'),
      ]);

      const termList = termRes.data as Term[];
      setTerms(termList);
      setTermId(termList[0]?.id);
      setTestTypes(typeRes.data);

      if (termList.length === 0) {
        setLoadError('No terms found for the active year. Confirm academics seeding has run.');
      }

      console.info('[marks] bootstrap', { year: yr.year, termCount: termList.length, testTypes: typeRes.data });
    } catch (error) {
      setLoadError(toMessage(error, 'Failed to load active year/terms/tasks setup data.'));
    }
  })(); }, [authReady, isAuthenticated]);

  useEffect(() => {
    if (!authReady || !isAuthenticated || !termId) return;
    api.get('/tasks', { params: { termId, gradeLevel, subjectId } })
      .then((r) => {
        setTasks(r.data);
        setTaskId(r.data[0]?.id);
        if (r.data.length === 0) {
          setLoadError('No assessment tasks found for the selected term. Confirm CAT/EXAM seeding.');
        }
      })
      .catch((error) => setLoadError(toMessage(error, 'Failed to load tasks for selected term.')));
  }, [authReady, isAuthenticated, termId, gradeLevel, subjectId]);

  useEffect(() => {
    if (!authReady || !isAuthenticated) return;
    api.get('/subjects', { params: { grade: gradeLevel } })
      .then((r) => {
        setSubjects(r.data);
        setSubjectId((current) => current ?? r.data[0]?.id);
        if (r.data.length === 0) {
          setLoadError('No subjects available. Seed subjects in the database and reload.');
        }
      })
      .catch((error) => setLoadError(toMessage(error, 'Failed to load subjects.')));
  }, [authReady, isAuthenticated, gradeLevel]);

  useEffect(() => {
    if (!authReady || !isAuthenticated) return;
    api.get('/learners', { params: { gradeLevel } }).then((r)=>setLearners(r.data));
  }, [authReady, isAuthenticated, gradeLevel]);

  useEffect(() => {
    if (!authReady || !isAuthenticated || !subjectId || !taskId) return;
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
  }, [authReady, isAuthenticated, subjectId, taskId, gradeLevel]);

  const rows = useMemo(()=>learners.map(l => ({...l, score: scores[l.id] ?? 0})), [learners, scores]);

  const save = async () => {
    if (!subjectId || !taskId) return;
    setLoadError('');
    try {
      await api.post('/marks/bulk', { subjectId, taskId, marks: rows.map(r => ({ learnerId: r.id, score: Number(r.score) })) });
      setSaveMessage('Marks saved successfully.');
      const statusResponse = await api.get('/marks/status', { params: { subjectId, taskId, gradeLevel } });
      setStatus(statusResponse.data.status);
    } catch (error) {
      setLoadError(toMessage(error, 'Unable to save marks.'));
    }
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
    {authLoading && <p>Loading authentication…</p>}
    {loadError && <p>{loadError}</p>}
    {saveMessage && <p>{saveMessage}</p>}
    {locked && <p>Submitted/Locked</p>}
    <article className='card form-grid'>
      <label>Year <input value={year ?? ''} disabled /></label>
      <label>Term <select value={termId} onChange={(e)=>setTermId(Number(e.target.value))}>{terms.map(t => <option key={t.id} value={t.id}>Term {t.termNo}</option>)}</select></label>
      <label>Task <select value={taskId} onChange={(e)=>setTaskId(Number(e.target.value))}>{tasks.map(t => <option key={t.id} value={t.id}>{t.title || t.type}</option>)}</select></label>
      <label>Grade <select value={gradeLevel} onChange={(e)=>setGradeLevel(Number(e.target.value))}>{[1,2,3,4,5,6,7].map(g=><option key={g}>{g}</option>)}</select></label>
      <label>Subject <select value={subjectId} onChange={(e)=>setSubjectId(Number(e.target.value))}>{subjects.map(s=><option key={s.id} value={s.id}>{s.name}</option>)}</select></label>
      <label>Test Types <input value={testTypes.join(', ')} disabled /></label>
    </article>
    <article className='card'>
      <table className='table'><thead><tr><th>Learner</th><th>Mark /50</th><th>Grade</th></tr></thead>
        <tbody>{rows.map(r => <tr key={r.id}><td>{r.admissionNo} - {r.firstName} {r.lastName}</td><td><input disabled={locked} type='number' min={0} max={50} value={r.score} onChange={(e)=>setScores({...scores, [r.id]: Number(e.target.value)})} /></td><td>{gradeOf(Number(r.score))}</td></tr>)}</tbody>
      </table>
      {rows.length === 0 && <p>No learners found for this grade or your assignment scope.</p>}
      <button className='btn btn-primary' disabled={locked || !taskId || !subjectId || rows.length === 0} onClick={save}>Save</button>
      {user?.role === 'TEACHER' && <button className='btn btn-secondary' disabled={locked} onClick={submit}>Submit</button>}
      {(user?.role === 'ADMIN' || user?.role === 'PRINCIPAL') && locked && <button className='btn btn-secondary' onClick={unlock}>Unlock</button>}
    </article>
  </section>;
};
