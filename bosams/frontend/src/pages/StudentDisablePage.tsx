import { useEffect, useState } from 'react';
import api from '../api/client';

type Learner = {
  id: number;
  admissionNo: string;
  firstName: string;
  lastName: string;
  gradeLevel: number;
  status: 'ACTIVE' | 'INACTIVE';
};

export const StudentDisablePage = () => {
  const [students, setStudents] = useState<Learner[]>([]);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const load = async () => {
    try {
      setError('');
      const response = await api.get<Learner[]>('/learners', { params: { activeOnly: false } });
      setStudents((response.data ?? []).filter((student) => student.status === 'INACTIVE'));
    } catch {
      setStudents([]);
      setError('Failed to load disabled students.');
    }
  };

  useEffect(() => { load(); }, []);

  const reactivate = async (id: number) => {
    try {
      setError('');
      await api.patch(`/learners/${id}/status`, { status: 'ACTIVE' });
      setMessage('Student reactivated.');
      await load();
    } catch {
      setError('Unable to reactivate student.');
    }
  };

  return (
    <section>
      <div className='page-header'>
        <div>
          <p className='section-eyebrow'>Students</p>
          <h2>Student Disable</h2>
        </div>
      </div>
      {message && <p className='muted'>{message}</p>}
      {error && <p className='muted'>{error}</p>}

      <article className='card'>
        <h3>Disabled Students</h3>
        {students.length === 0 && <p>No disabled students found.</p>}
        <table className='table'>
          <thead><tr><th>Admission</th><th>Name</th><th>Grade</th><th>Action</th></tr></thead>
          <tbody>
            {students.map((student) => (
              <tr key={student.id}>
                <td>{student.admissionNo}</td>
                <td>{student.firstName} {student.lastName}</td>
                <td>{student.gradeLevel}</td>
                <td><button className='btn btn-primary' onClick={() => reactivate(student.id)}>Reactivate</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </article>
    </section>
  );
};
