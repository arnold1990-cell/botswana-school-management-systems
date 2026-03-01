import { useEffect, useState } from 'react';
import api from '../api/client';

const metricCards = [
  { label: 'Students', value: '2,148', trend: '+4.2%' },
  { label: 'Teachers', value: '134', trend: '+1.4%' },
  { label: 'Attendance', value: '96.3%', trend: '+0.8%' },
  { label: 'Pending Reports', value: '18', trend: '-6%' },
];

const PageHeader = ({ title, subtitle, actionLabel }: { title: string; subtitle: string; actionLabel: string }) => (
  <div className='page-header'>
    <div>
      <h2>{title}</h2>
      <p className='muted'>{subtitle}</p>
    </div>
    <button className='btn btn-primary' type='button'>
      {actionLabel}
    </button>
  </div>
);

export const DashboardPage = () => (
  <section>
    <PageHeader title='Dashboard' subtitle='Overview of school operations and activity.' actionLabel='Generate Summary' />
    <div className='card-grid'>
      {metricCards.map((metric) => (
        <article key={metric.label} className='card metric-card'>
          <p className='muted'>{metric.label}</p>
          <h3>{metric.value}</h3>
          <span className='badge'>{metric.trend}</span>
        </article>
      ))}
    </div>
  </section>
);

export const TeacherDashboardPage = () => {
  const [assignments, setAssignments] = useState<any[]>([]);

  useEffect(() => {
    api.get('/teacher/my-assignments').then((res) => setAssignments(res.data));
  }, []);

  return (
    <section>
      <PageHeader title='Teacher Dashboard' subtitle='Track your assigned grades and subjects.' actionLabel='My Assignments' />
      <article className='card'>
        <h3>My Assignments</h3>
        <table className='table'>
          <thead><tr><th>Grade</th><th>Subject</th><th>Academic Year</th></tr></thead>
          <tbody>{assignments.map((assignment) => <tr key={assignment.id}><td>{assignment.gradeLevel}</td><td>{assignment.subject.name}</td><td>{assignment.academicYear.year}</td></tr>)}</tbody>
        </table>
      </article>
    </section>
  );
};
