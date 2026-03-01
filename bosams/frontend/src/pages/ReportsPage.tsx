import { useEffect, useState } from 'react';
import api from '../api/client';

type Subject = { id: number; name: string };
type Row = { learnerName: string; catScore?: number; examScore?: number; total: number; finalGrade: string };

export const ReportsPage = () => {
  const [year, setYear] = useState<number>();
  const [termNumber, setTermNumber] = useState(1);
  const [gradeLevel, setGradeLevel] = useState(1);
  const [subjectId, setSubjectId] = useState<number>();
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [rows, setRows] = useState<Row[]>([]);

  useEffect(() => { (async () => {
    const y = (await api.get('/academic-years/current')).data.year;
    setYear(y);
    const sub = (await api.get('/subjects')).data;
    setSubjects(sub);
    setSubjectId(sub[0]?.id);
  })(); }, []);

  const load = async () => {
    if (!year || !subjectId) return;
    const res = await api.get('/reports/term', { params: { year, termNumber, gradeLevel, subjectId } });
    setRows(res.data);
  };

  return <section>
    <h2>Reports</h2>
    <article className='card form-grid'>
      <label>Year <input value={year ?? ''} disabled /></label>
      <label>Term <select value={termNumber} onChange={(e)=>setTermNumber(Number(e.target.value))}>{[1,2,3].map(t => <option key={t}>{t}</option>)}</select></label>
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
