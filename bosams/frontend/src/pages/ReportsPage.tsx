import { useEffect, useState } from 'react';
import api from '../api/client';

type Subject = { id: number; name: string };
type Term = { id: number; termNo: number };
type Row = { learnerName: string; catScore?: number; examScore?: number; total: number; finalGrade: string };

export const ReportsPage = () => {
  const [year, setYear] = useState<number>();
  const [termNumber, setTermNumber] = useState(1);
  const [terms, setTerms] = useState<Term[]>([]);
  const [gradeLevel, setGradeLevel] = useState(1);
  const [subjectId, setSubjectId] = useState<number>();
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [rows, setRows] = useState<Row[]>([]);
  const [error, setError] = useState('');

  useEffect(() => { (async () => {
    try {
      const y = (await api.get('/academic-years/current')).data.year;
      setYear(y);
      const [termResponse, subResponse] = await Promise.all([
        api.get('/terms', { params: { year: y } }),
        api.get('/subjects'),
      ]);
      setTerms(termResponse.data ?? []);
      setTermNumber(termResponse.data?.[0]?.termNo ?? 1);
      setSubjects(subResponse.data ?? []);
      setSubjectId(subResponse.data?.[0]?.id);
    } catch {
      setError('Failed to load reports setup.');
    }
  })(); }, []);

  const load = async () => {
    if (!year || !subjectId) return;
    try {
      setError('');
      const res = await api.get('/reports/term', { params: { year, termNumber, gradeLevel, subjectId } });
      setRows(res.data);
    } catch {
      setError('Unable to load report for selected filters.');
      setRows([]);
    }
  };

  return <section>
    <h2>Reports</h2>
    {error && <p className='muted'>{error}</p>}
    <article className='card form-grid'>
      <label>Year <input value={year ?? ''} disabled /></label>
      <label>Term <select value={termNumber} onChange={(e)=>setTermNumber(Number(e.target.value))}>{terms.map(t => <option key={t.id} value={t.termNo}>Term {t.termNo}</option>)}</select></label>
      <label>Grade <select value={gradeLevel} onChange={(e)=>setGradeLevel(Number(e.target.value))}>{[1,2,3,4,5,6,7].map(g => <option key={g}>{g}</option>)}</select></label>
      <label>Subject <select value={subjectId} onChange={(e)=>setSubjectId(Number(e.target.value))}>{subjects.map(s=><option key={s.id} value={s.id}>{s.name}</option>)}</select></label>
      <button className='btn btn-primary' onClick={load}>Load report</button>
      <button className='btn btn-secondary' type='button'>Export PDF (placeholder)</button>
    </article>
    <article className='card'>
      <table className='table'><thead><tr><th>Learner</th><th>CAT</th><th>Exam</th><th>Total</th><th>Grade</th></tr></thead>
        <tbody>{rows.map((r, i)=><tr key={i}><td>{r.learnerName}</td><td>{r.catScore ?? '-'}</td><td>{r.examScore ?? '-'}</td><td>{r.total}</td><td>{r.finalGrade}</td></tr>)}</tbody></table>
    </article>
  </section>;
};
