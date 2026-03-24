import { FormEvent, useEffect, useState } from 'react';
import api from '../api/client';
import { useAuth } from '../auth/AuthContext';

type Learner = {
  id: number;
  admissionNo: string;
  firstName: string;
  lastName: string;
  gender: string;
  gradeLevel: number;
  rollNumber?: number;
  studentCategory?: string;
  guardianName?: string;
  guardianPhone?: string;
  guardianEmail?: string;
  status: 'ACTIVE' | 'INACTIVE';
};

export const LearnersPage = () => {
  const { user } = useAuth();
  const [learners, setLearners] = useState<Learner[]>([]);
  const [gradeLevel, setGradeLevel] = useState('ALL');
  const [query, setQuery] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    admissionNo: '',
    firstName: '',
    lastName: '',
    gender: 'MALE',
    gradeLevel: 1,
    studentCategory: 'General',
    guardianName: '',
    guardianPhone: '',
    guardianEmail: '',
  });
  const [editingId, setEditingId] = useState<number | null>(null);

  const load = async (grade?: string, q?: string) => {
    setLoading(true);
    setError('');
    const params = {
      ...(grade && grade !== 'ALL' ? { gradeLevel: Number(grade) } : {}),
      ...(q && q.trim().length > 0 ? { query: q.trim() } : {}),
    };
    try {
      const res = await api.get('/learners', { params });
      setLearners(res.data ?? []);
    } catch {
      setLearners([]);
      setError('Could not load learners. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load(gradeLevel, query);
  }, [gradeLevel]);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    await api.post('/learners', { ...form, schoolId: 1 });
    setMessage('Learner admission saved successfully.');
    setForm({ ...form, admissionNo: '', firstName: '', lastName: '', guardianName: '', guardianPhone: '', guardianEmail: '' });
    await load(gradeLevel, query);
  };

  const updateLearner = async (learner: Learner) => {
    await api.put(`/learners/${learner.id}`, {
      firstName: learner.firstName,
      lastName: learner.lastName,
      gender: learner.gender,
      gradeLevel: learner.gradeLevel,
      studentCategory: learner.studentCategory,
      guardianName: learner.guardianName,
      guardianPhone: learner.guardianPhone,
      guardianEmail: learner.guardianEmail,
      status: learner.status,
    });
    setEditingId(null);
    setMessage('Learner profile updated.');
    await load(gradeLevel, query);
  };

  const assignRoll = async (learner: Learner, rollNumber: number) => {
    await api.patch(`/learners/${learner.id}/roll-number`, { rollNumber });
    setMessage(`Roll number #${rollNumber} assigned for ${learner.firstName}.`);
    await load(gradeLevel, query);
  };

  return <section>
    <div className='page-header'>
      <div>
        <p className='section-eyebrow'>Students</p>
        <h2>Students Admission & Details</h2>
      </div>
    </div>

    {message && <p className='muted'>{message}</p>}

    {(user?.role === 'ADMIN' || user?.role === 'PRINCIPAL') && <form className='card form-grid form-grid-3' onSubmit={submit}>
      <input placeholder='Admission No' value={form.admissionNo} onChange={(e) => setForm({ ...form, admissionNo: e.target.value })} required />
      <input placeholder='First Name' value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} required />
      <input placeholder='Last Name' value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} required />
      <select value={form.gender} onChange={(e) => setForm({ ...form, gender: e.target.value })}><option>MALE</option><option>FEMALE</option></select>
      <select value={form.gradeLevel} onChange={(e) => setForm({ ...form, gradeLevel: Number(e.target.value) })}>{[1, 2, 3, 4, 5, 6, 7].map(g => <option key={g} value={g}>Grade {g}</option>)}</select>
      <input placeholder='Student Category' value={form.studentCategory} onChange={(e) => setForm({ ...form, studentCategory: e.target.value })} />
      <input placeholder='Guardian Name' value={form.guardianName} onChange={(e) => setForm({ ...form, guardianName: e.target.value })} />
      <input placeholder='Guardian Phone' value={form.guardianPhone} onChange={(e) => setForm({ ...form, guardianPhone: e.target.value })} />
      <input placeholder='Guardian Email' type='email' value={form.guardianEmail} onChange={(e) => setForm({ ...form, guardianEmail: e.target.value })} />
      <div>
        <button className='btn btn-primary' type='submit'>Create Learner</button>
      </div>
    </form>}

    <div className='card'>
      {error && <p className='muted'>{error}</p>}
      <div className='toolbar'>
        <label>Grade
          <select value={gradeLevel} onChange={(e) => setGradeLevel(e.target.value)}>
            <option value='ALL'>All</option>
            {[1, 2, 3, 4, 5, 6, 7].map(g => <option key={g} value={g}>{g}</option>)}
          </select>
        </label>
        <input value={query} onChange={(e) => setQuery(e.target.value)} placeholder='Search by admission, name, guardian, category' />
        <button className='btn btn-secondary' onClick={() => load(gradeLevel, query)}>Search</button>
      </div>
      {loading && <p>Loading learners...</p>}
      {!loading && <table className='table'>
        <thead><tr><th>Admission</th><th>Name</th><th>Grade</th><th>Roll No.</th><th>Category</th><th>Guardian</th><th>Action</th></tr></thead>
        <tbody>{learners.map(l => {
          const editable = editingId === l.id;
          return <tr key={l.id}><td>{l.admissionNo}</td><td>
            {editable ? <>
              <input value={l.firstName} onChange={(e) => setLearners(prev => prev.map(x => x.id === l.id ? { ...x, firstName: e.target.value } : x))} />
              <input value={l.lastName} onChange={(e) => setLearners(prev => prev.map(x => x.id === l.id ? { ...x, lastName: e.target.value } : x))} />
            </> : `${l.firstName} ${l.lastName}`}
          </td><td>
              {editable ? <select value={l.gradeLevel} onChange={(e) => setLearners(prev => prev.map(x => x.id === l.id ? { ...x, gradeLevel: Number(e.target.value) } : x))}>{[1, 2, 3, 4, 5, 6, 7].map(g => <option key={g} value={g}>{g}</option>)}</select> : l.gradeLevel}
            </td>
            <td>
              <div className='roll-badge'>#{l.rollNumber ?? '--'}</div>
              {(user?.role === 'ADMIN' || user?.role === 'PRINCIPAL') && <button className='btn btn-pill' onClick={() => assignRoll(l, l.rollNumber ? l.rollNumber + 1 : 1)}>Assign</button>}
            </td>
            <td>{editable ? <input value={l.studentCategory ?? ''} onChange={(e) => setLearners(prev => prev.map(x => x.id === l.id ? { ...x, studentCategory: e.target.value } : x))} /> : l.studentCategory ?? '--'}</td>
            <td>{editable ? <>
              <input value={l.guardianName ?? ''} onChange={(e) => setLearners(prev => prev.map(x => x.id === l.id ? { ...x, guardianName: e.target.value } : x))} />
              <input value={l.guardianPhone ?? ''} onChange={(e) => setLearners(prev => prev.map(x => x.id === l.id ? { ...x, guardianPhone: e.target.value } : x))} />
            </> : <>{l.guardianName ?? '--'}<br />{l.guardianPhone ?? ''}</>}</td>
            <td>
              {(user?.role === 'ADMIN' || user?.role === 'PRINCIPAL') && (editable
                ? <button className='btn btn-primary' onClick={() => updateLearner(l)}>Save</button>
                : <button className='btn btn-secondary' onClick={() => setEditingId(l.id)}>Edit</button>)}
            </td></tr>;
        })}</tbody>
      </table>}
      {!loading && learners.length === 0 && <p>No learners found for selected filters.</p>}
    </div>
  </section>;
};
