import { useEffect, useState } from 'react';
import api from '../api/client';
import { useAuthReady } from '../auth/useAuthReady';

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
  const { authReady, authLoading, isAuthenticated } = useAuthReady();
  const [assignments, setAssignments] = useState<any[]>([]);

  useEffect(() => {
    if (!authReady || !isAuthenticated) return;
    api.get('/teacher/my-assignments').then((res) => setAssignments(res.data));
  }, [authReady, isAuthenticated]);

  return (
    <section>
      <PageHeader title='Teacher Dashboard' subtitle='Track your assigned grades and subjects.' actionLabel='My Assignments' />
      {authLoading && <p className='muted'>Loading authentication…</p>}
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

export const StudentDashboardPage = () => (
  <section>
    <PageHeader title='Student Dashboard' subtitle='Track learning progress, attendance, and assignments.' actionLabel='View Assignments' />
    <div className='card-grid'>
      <article className='card metric-card'><p className='muted'>Subjects</p><h3>6</h3><span className='badge'>Current Term</span></article>
      <article className='card metric-card'><p className='muted'>Assignments Due</p><h3>3</h3><span className='badge'>This Week</span></article>
      <article className='card metric-card'><p className='muted'>Attendance</p><h3>95%</h3><span className='badge'>Term to Date</span></article>
      <article className='card metric-card'><p className='muted'>Announcements</p><h3>4</h3><span className='badge'>Unread</span></article>
    </div>
  </section>
);

export const ParentDashboardPage = () => (
  <section>
    <PageHeader title='Parent Dashboard' subtitle='Monitor child performance, attendance, and school updates.' actionLabel='View Child Report' />
    <div className='card-grid'>
      <article className='card metric-card'><p className='muted'>Child Attendance</p><h3>94%</h3><span className='badge'>This Month</span></article>
      <article className='card metric-card'><p className='muted'>Outstanding Fees</p><h3>BWP 1,240</h3><span className='badge'>Current Session</span></article>
      <article className='card metric-card'><p className='muted'>Assignments Pending</p><h3>2</h3><span className='badge'>Due Soon</span></article>
      <article className='card metric-card'><p className='muted'>Announcements</p><h3>5</h3><span className='badge'>New</span></article>
    </div>
  </section>
);

export const AccountantDashboardPage = () => (
  <section>
    <PageHeader title='Accountant Dashboard' subtitle='Track school collections, balances, and transactions.' actionLabel='Export Transactions' />
    <div className='card-grid'>
      <article className='card metric-card'><p className='muted'>Payments Received</p><h3>BWP 128,450</h3><span className='badge'>Month to Date</span></article>
      <article className='card metric-card'><p className='muted'>Outstanding Balance</p><h3>BWP 34,120</h3><span className='badge'>Current Session</span></article>
      <article className='card metric-card'><p className='muted'>Transactions</p><h3>412</h3><span className='badge'>Recorded</span></article>
      <article className='card metric-card'><p className='muted'>Receipts Generated</p><h3>398</h3><span className='badge'>Issued</span></article>
    </div>
  </section>
);
