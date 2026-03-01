import { FormEvent, useEffect, useState } from 'react';
import api from '../api/client';

type Learner = { id: number; admissionNo: string; firstName: string; lastName: string; gender: string; gradeLevel: number };

export const LearnersPage = () => {
  const [learners, setLearners] = useState<Learner[]>([]);
  const [gradeLevel, setGradeLevel] = useState('1');
  const [form, setForm] = useState({ admissionNo: '', firstName: '', lastName: '', gender: 'MALE', gradeLevel: 1 });

  const load = async (grade?: string) => {
    const res = await api.get('/learners', { params: grade ? { gradeLevel: grade } : {} });
    setLearners(res.data);
  };

  useEffect(() => { load(gradeLevel); }, []);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    await api.post('/learners', form);
    setForm({ admissionNo: '', firstName: '', lastName: '', gender: 'MALE', gradeLevel: Number(gradeLevel) });
    await load(gradeLevel);
  };

  return <section>
    <h2>Learners Registration</h2>
    <form className='card form-grid' onSubmit={submit}>
      <input placeholder='Admission No' value={form.admissionNo} onChange={(e)=>setForm({...form, admissionNo: e.target.value})} required />
      <input placeholder='First Name' value={form.firstName} onChange={(e)=>setForm({...form, firstName: e.target.value})} required />
      <input placeholder='Last Name' value={form.lastName} onChange={(e)=>setForm({...form, lastName: e.target.value})} required />
      <select value={form.gender} onChange={(e)=>setForm({...form, gender: e.target.value})}><option>MALE</option><option>FEMALE</option></select>
      <select value={form.gradeLevel} onChange={(e)=>setForm({...form, gradeLevel: Number(e.target.value)})}>{[1,2,3,4,5,6,7].map(g=><option key={g} value={g}>Grade {g}</option>)}</select>
      <button className='btn btn-primary' type='submit'>Create Learner</button>
    </form>

    <div className='card'>
      <label>Filter Grade <select value={gradeLevel} onChange={async (e)=>{setGradeLevel(e.target.value); await load(e.target.value);}}>{[1,2,3,4,5,6,7].map(g=><option key={g} value={g}>{g}</option>)}</select></label>
      <table className='table'>
        <thead><tr><th>Admission</th><th>Name</th><th>Gender</th><th>Grade</th></tr></thead>
        <tbody>{learners.map(l=><tr key={l.id}><td>{l.admissionNo}</td><td>{l.firstName} {l.lastName}</td><td>{l.gender}</td><td>{l.gradeLevel}</td></tr>)}</tbody>
      </table>
    </div>
  </section>;
};
