import { useEffect, useState } from 'react';
import api from '../api/client';

type Subject = { id: number; name: string; code?: string; schoolLevel: 'PRIMARY' | 'JUNIOR_SECONDARY' | 'SENIOR_SECONDARY'; gradeFrom: number; gradeTo: number };

export const SubjectsPage = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [level, setLevel] = useState<'ALL' | 'PRIMARY' | 'JUNIOR_SECONDARY' | 'SENIOR_SECONDARY'>('ALL');
  const [classLevel, setClassLevel] = useState('ALL');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const load = async (selectedLevel: string, selectedClassLevel: string) => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get('/subjects', { params: { ...(selectedLevel !== 'ALL' ? { level: selectedLevel } : {}), ...(selectedClassLevel !== 'ALL' ? { grade: Number(selectedClassLevel) } : {}) } });
      setSubjects(response.data ?? []);
    } catch {
      setError('Unable to load subjects.');
      setSubjects([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(level, classLevel); }, [level, classLevel]);

  return <section>
    <h2>Subjects</h2>
    <article className='card'>
      <div className='toolbar'>
        <label>Level
          <select value={level} onChange={(e) => setLevel(e.target.value as 'ALL' | 'PRIMARY' | 'JUNIOR_SECONDARY' | 'SENIOR_SECONDARY')}>
            <option value='ALL'>All</option>
            <option value='PRIMARY'>Primary</option>
            <option value='JUNIOR_SECONDARY'>Junior Secondary</option>
            <option value='SENIOR_SECONDARY'>Senior Secondary</option>
          </select>
        </label>
        <label>Class
          <select value={classLevel} onChange={(e) => setClassLevel(e.target.value)}>
            <option value='ALL'>All</option>
            {[1, 2, 3, 4, 5, 6, 7].map((grade) => <option key={grade} value={grade}>Std {grade}</option>)}
            {[8, 9, 10, 11, 12].map((grade) => <option key={grade} value={grade}>Form {grade - 7}</option>)}
          </select>
        </label>
      </div>
      {error && <p className='muted'>{error}</p>}
      {loading && <p>Loading subjects...</p>}
      <table className='table'>
        <thead><tr><th>Name</th><th>Code</th><th>Level</th><th>Class Range</th></tr></thead>
        <tbody>{subjects.map(s => <tr key={s.id}><td>{s.name}</td><td>{s.code ?? '-'}</td><td>{s.schoolLevel}</td><td>{s.gradeFrom === s.gradeTo ? s.gradeFrom : `${s.gradeFrom}-${s.gradeTo}`}</td></tr>)}</tbody>
      </table>
      {!loading && subjects.length === 0 && <p>No subjects found for this filter.</p>}
    </article>
  </section>;
};
