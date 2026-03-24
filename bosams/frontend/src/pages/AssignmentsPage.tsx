import { FormEvent, useEffect, useState } from 'react';
import api from '../api/client';
import { useAuthReady } from '../auth/useAuthReady';

type Term = { id: number; termNo: number };
type Subject = { id: number; name: string };
type Assignment = { id: number; title: string; description?: string; dueDate?: string; maxScore: number; gradeLevel?: number; type: string; subject?: Subject };

export const AssignmentsPage = () => {
  const { authReady, authLoading } = useAuthReady();
  const [terms, setTerms] = useState<Term[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [termId, setTermId] = useState<number>();
  const [subjectId, setSubjectId] = useState<number>();
  const [gradeLevel, setGradeLevel] = useState(1);
  const [items, setItems] = useState<Assignment[]>([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({ title: '', description: '', dueDate: '', totalMarks: 50 });

  const loadSetup = async () => {
    const year = (await api.get('/academics/active-year')).data.year;
    const [termRes, subjectRes] = await Promise.all([
      api.get('/terms', { params: { year } }),
      api.get('/subjects', { params: { grade: gradeLevel } }),
    ]);
    setTerms(termRes.data);
    setSubjects(subjectRes.data);
    setTermId((current: number | undefined) => current ?? termRes.data[0]?.id);
    setSubjectId((current: number | undefined) => current ?? subjectRes.data[0]?.id);
  };

  const loadAssignments = async (selectedTermId?: number, selectedSubjectId?: number) => {
    if (!selectedTermId || !selectedSubjectId) return;
    setLoading(true);
    setError('');
    try {
      const response = await api.get('/tasks', { params: { termId: selectedTermId, subjectId: selectedSubjectId, gradeLevel } });
      setItems((response.data ?? []).filter((task: Assignment) => task.type === 'ASSIGNMENT'));
    } catch {
      setError('Failed to load assignments.');
      setItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!authReady) return;
    loadSetup().catch(() => setError('Failed to load assignment setup.'));
  }, [authReady, gradeLevel]);

  useEffect(() => {
    if (!authReady) return;
    loadAssignments(termId, subjectId);
  }, [authReady, termId, subjectId, gradeLevel]);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    if (!termId || !subjectId || !form.title.trim()) return;
    try {
      setError('');
      await api.post('/tasks', {
        termId,
        subjectId,
        gradeLevel,
        type: 'ASSIGNMENT',
        title: form.title.trim(),
        description: form.description.trim(),
        dueDate: form.dueDate || null,
        maxScore: Number(form.totalMarks || 50),
      });
      setMessage('Assignment created successfully.');
      setForm({ title: '', description: '', dueDate: '', totalMarks: 50 });
      await loadAssignments(termId, subjectId);
    } catch {
      setError('Unable to create assignment. Please check required values and try again.');
    }
  };

  return <section>
    <h2>Assignments</h2>
    {authLoading && <p>Loading authentication…</p>}
    {message && <p className='muted'>{message}</p>}
    {error && <p className='muted'>{error}</p>}

    <form className='card form-grid form-grid-3' onSubmit={submit}>
      <label>Class
        <select value={gradeLevel} onChange={(e) => setGradeLevel(Number(e.target.value))}>{[1, 2, 3, 4, 5, 6, 7].map((g) => <option key={g} value={g}>Grade {g}</option>)}</select>
      </label>
      <label>Term
        <select value={termId} onChange={(e) => setTermId(Number(e.target.value))}>{terms.map((term) => <option key={term.id} value={term.id}>Term {term.termNo}</option>)}</select>
      </label>
      <label>Subject
        <select value={subjectId} onChange={(e) => setSubjectId(Number(e.target.value))}>{subjects.map((subject) => <option key={subject.id} value={subject.id}>{subject.name}</option>)}</select>
      </label>
      <input placeholder='Title' required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
      <input placeholder='Description' value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
      <label>Due Date <input type='date' value={form.dueDate} onChange={(e) => setForm({ ...form, dueDate: e.target.value })} /></label>
      <label>Total Marks <input type='number' min={1} max={100} value={form.totalMarks} onChange={(e) => setForm({ ...form, totalMarks: Number(e.target.value) })} /></label>
      <button className='btn btn-primary' type='submit'>Create Assignment</button>
    </form>

    <article className='card'>
      <h3>Assignments List</h3>
      {loading && <p>Loading assignments...</p>}
      {!loading && items.length === 0 && <p>No assignments available for this class/subject/term.</p>}
      <table className='table'>
        <thead><tr><th>Title</th><th>Description</th><th>Class</th><th>Due Date</th><th>Total Marks</th></tr></thead>
        <tbody>{items.map((item) => <tr key={item.id}><td>{item.title}</td><td>{item.description || '-'}</td><td>{item.gradeLevel ? `Grade ${item.gradeLevel}` : '-'}</td><td>{item.dueDate || '-'}</td><td>{item.maxScore}</td></tr>)}</tbody>
      </table>
    </article>
  </section>;
};
