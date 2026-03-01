import { useEffect, useState } from 'react';
import api from '../api/client';

type Subject = { id: number; name: string; code?: string };

export const SubjectsPage = () => {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  useEffect(() => { api.get('/subjects').then(r => setSubjects(r.data)); }, []);

  return <section>
    <h2>Subjects</h2>
    <article className='card'>
      <table className='table'>
        <thead><tr><th>Name</th><th>Code</th></tr></thead>
        <tbody>{subjects.map(s => <tr key={s.id}><td>{s.name}</td><td>{s.code ?? '-'}</td></tr>)}</tbody>
      </table>
    </article>
  </section>;
};
