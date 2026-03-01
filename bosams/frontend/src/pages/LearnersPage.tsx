import { FormEvent, useEffect, useState } from 'react';
import api from '../api/client';
import { useAuth } from '../auth/AuthContext';

type Learner = { id: number; admissionNo: string; firstName: string; lastName: string; gender: string; gradeLevel: number };

export const LearnersPage = () => {
  const { user } = useAuth();
  const [learners, setLearners] = useState<Learner[]>([]);
  const [gradeLevel, setGradeLevel] = useState('1');
  const [message, setMessage] = useState('');
  const [form, setForm] = useState({ admissionNo: '', firstName: '', lastName: '', gender: 'MALE', gradeLevel: 1 });
  const [editingId, setEditingId] = useState<number | null>(null);

  const load = async (grade?: string) => {
    const res = await api.get('/learners', { params: grade ? { gradeLevel: grade } : {} });
    setLearners(res.data);
  };

  useEffect(() => { load(gradeLevel); }, []);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    await api.post('/learners', form);
    setMessage('Learner created successfully.');
    setForm({ admissionNo: '', firstName: '', lastName: '', gender: 'MALE', gradeLevel: Number(gradeLevel) });
    await load(gradeLevel);
  };

  const updateLearner = async (learner: Learner) => {
    await api.put(`/learners/${learner.id}`, {
      firstName: learner.firstName,
      lastName: learner.lastName,
      gender: learner.gender,
      gradeLevel: learner.gradeLevel,
    });
    setEditingId(null);
    setMessage('Learner information saved.');
    await load(gradeLevel);
  };

  return <section>
    <h2>Learners</h2>
    {message && <p>{message}</p>}
    {(user?.role === 'ADMIN' || user?.role === 'PRINCIPAL') && <form className='card form-grid' onSubmit={submit}>
      <input placeholder='Admission No' value={form.admissionNo} onChange={(e)=>setForm({...form, admissionNo: e.target.value})} required />
      <input placeholder='First Name' value={form.firstName} onChange={(e)=>setForm({...form, firstName: e.target.value})} required />
      <input placeholder='Last Name' value={form.lastName} onChange={(e)=>setForm({...form, lastName: e.target.value})} required />
      <select value={form.gender} onChange={(e)=>setForm({...form, gender: e.target.value})}><option>MALE</option><option>FEMALE</option></select>
      <select value={form.gradeLevel} onChange={(e)=>setForm({...form, gradeLevel: Number(e.target.value)})}>{[1,2,3,4,5,6,7].map(g=><option key={g} value={g}>Grade {g}</option>)}</select>
      <button className='btn btn-primary' type='submit'>Create Learner</button>
    </form>}

    <div className='card'>
      <label>Filter Grade <select value={gradeLevel} onChange={async (e)=>{setGradeLevel(e.target.value); await load(e.target.value);}}>{[1,2,3,4,5,6,7].map(g=><option key={g} value={g}>{g}</option>)}</select></label>
      <table className='table'>
        <thead><tr><th>Admission</th><th>Name</th><th>Gender</th><th>Grade</th><th></th></tr></thead>
        <tbody>{learners.map(l=>{
          const editable = editingId === l.id;
          return <tr key={l.id}><td>{l.admissionNo}</td><td>
              {editable ? <>
                <input value={l.firstName} onChange={(e)=>setLearners(prev=>prev.map(x=>x.id===l.id?{...x, firstName:e.target.value}:x))}/>
                <input value={l.lastName} onChange={(e)=>setLearners(prev=>prev.map(x=>x.id===l.id?{...x, lastName:e.target.value}:x))}/>
              </> : `${l.firstName} ${l.lastName}`}
            </td><td>{editable ? <select value={l.gender} onChange={(e)=>setLearners(prev=>prev.map(x=>x.id===l.id?{...x, gender:e.target.value}:x))}><option>MALE</option><option>FEMALE</option></select> : l.gender}</td><td>{editable ? <select value={l.gradeLevel} onChange={(e)=>setLearners(prev=>prev.map(x=>x.id===l.id?{...x, gradeLevel:Number(e.target.value)}:x))}>{[1,2,3,4,5,6,7].map(g=><option key={g} value={g}>{g}</option>)}</select> : l.gradeLevel}</td><td>{(user?.role === 'ADMIN' || user?.role === 'PRINCIPAL') && (editable ? <button className='btn btn-primary' onClick={()=>updateLearner(l)}>Save</button> : <button className='btn btn-secondary' onClick={()=>setEditingId(l.id)}>Edit</button>)}</td></tr>;
        })}</tbody>
      </table>
    </div>
  </section>;
};
