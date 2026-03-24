import { useEffect, useMemo, useState } from 'react';
import api from '../api/client';

type Subject = {
  id: number;
  name: string;
  code?: string;
  schoolLevel: 'PRIMARY' | 'JUNIOR_SECONDARY' | 'SENIOR_SECONDARY';
  gradeFrom: number;
  gradeTo: number;
};

type LevelFilter = 'ALL' | Subject['schoolLevel'];

const allClassOptions = [
  ...Array.from({ length: 7 }, (_, i) => ({ value: String(i + 1), label: `Standard ${i + 1}` })),
  ...Array.from({ length: 5 }, (_, i) => ({ value: String(i + 8), label: `Form ${i + 1}` })),
];

const toErrorMessage = (error: unknown) => {
  const apiMessage = (error as { response?: { data?: { message?: string } } })?.response?.data?.message;
  if (typeof apiMessage === 'string' && apiMessage.trim()) {
    return apiMessage;
  }
  return 'Unable to load subjects. Please try again.';
};

const normalizeSubjects = (payload: unknown): Subject[] => {
  if (Array.isArray(payload)) {
    return payload as Subject[];
  }

  if (payload && typeof payload === 'object') {
    const container = payload as { data?: unknown; items?: unknown; content?: unknown };
    if (Array.isArray(container.data)) return container.data as Subject[];
    if (Array.isArray(container.items)) return container.items as Subject[];
    if (Array.isArray(container.content)) return container.content as Subject[];
  }

  return [];
};

export const SubjectsPage = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [level, setLevel] = useState<LevelFilter>('ALL');
  const [classLevel, setClassLevel] = useState('ALL');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const classOptions = useMemo(() => {
    if (level === 'PRIMARY') return allClassOptions.filter((option) => Number(option.value) <= 7);
    if (level === 'JUNIOR_SECONDARY') return allClassOptions.filter((option) => Number(option.value) >= 8 && Number(option.value) <= 10);
    if (level === 'SENIOR_SECONDARY') return allClassOptions.filter((option) => Number(option.value) >= 11);
    return allClassOptions;
  }, [level]);

  useEffect(() => {
    if (classLevel !== 'ALL' && !classOptions.some((option) => option.value === classLevel)) {
      setClassLevel('ALL');
    }
  }, [classLevel, classOptions]);

  const load = async (selectedLevel: LevelFilter, selectedClassLevel: string) => {
    setLoading(true);
    setError('');

    try {
      const response = await api.get('/subjects', {
        params: {
          ...(selectedLevel !== 'ALL' ? { level: selectedLevel } : {}),
          ...(selectedClassLevel !== 'ALL' ? { grade: Number(selectedClassLevel) } : {}),
        },
      });
      setSubjects(normalizeSubjects(response.data));
    } catch (requestError) {
      setError(toErrorMessage(requestError));
      setSubjects([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load(level, classLevel);
  }, [level, classLevel]);

  return <section>
    <h2>Subjects</h2>
    <article className='card'>
      <div className='toolbar'>
        <label>Level
          <select value={level} onChange={(e) => setLevel(e.target.value as LevelFilter)}>
            <option value='ALL'>All</option>
            <option value='PRIMARY'>Primary</option>
            <option value='JUNIOR_SECONDARY'>Junior Secondary</option>
            <option value='SENIOR_SECONDARY'>Senior Secondary</option>
          </select>
        </label>
        <label>Class
          <select value={classLevel} onChange={(e) => setClassLevel(e.target.value)}>
            <option value='ALL'>All</option>
            {classOptions.map((grade) => <option key={grade.value} value={grade.value}>{grade.label}</option>)}
          </select>
        </label>
      </div>

      {error && <p className='muted'>{error}</p>}
      {error && <button className='btn btn-secondary' type='button' onClick={() => load(level, classLevel)}>Retry</button>}
      {loading && <p>Loading subjects...</p>}

      <table className='table'>
        <thead><tr><th>Name</th><th>Code</th><th>Level</th><th>Class Range</th></tr></thead>
        <tbody>{subjects.map((s) => <tr key={s.id}><td>{s.name}</td><td>{s.code ?? '-'}</td><td>{s.schoolLevel.replace(/_/g, ' ')}</td><td>{s.gradeFrom === s.gradeTo ? s.gradeFrom : `${s.gradeFrom}-${s.gradeTo}`}</td></tr>)}</tbody>
      </table>

      {!loading && !error && subjects.length === 0 && <p>No subjects found for this filter.</p>}
    </article>
  </section>;
};
