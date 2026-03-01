const metricCards = [
  { label: 'Students', value: '2,148', trend: '+4.2%' },
  { label: 'Teachers', value: '134', trend: '+1.4%' },
  { label: 'Attendance', value: '96.3%', trend: '+0.8%' },
  { label: 'Pending Reports', value: '18', trend: '-6%' },
];

const classes = [
  { class: 'Form 1A', teacher: 'M. Ndlovu', students: 38, attendance: '97%' },
  { class: 'Form 2B', teacher: 'P. Moagi', students: 41, attendance: '95%' },
  { class: 'Form 3C', teacher: 'T. Baitshepi', students: 36, attendance: '98%' },
];

const examSchedule = [
  { subject: 'Mathematics', date: '12 Mar 2026', class: 'Form 4', status: 'Scheduled' },
  { subject: 'English', date: '14 Mar 2026', class: 'Form 2', status: 'Published' },
  { subject: 'Biology', date: '17 Mar 2026', class: 'Form 3', status: 'Draft' },
];

const teachers = [
  { name: 'Masego Ndlovu', department: 'Sciences', workload: '24 hrs', status: 'Active' },
  { name: 'Kefilwe Motlaleng', department: 'Languages', workload: '20 hrs', status: 'Active' },
  { name: 'Thato Moalosi', department: 'Humanities', workload: '18 hrs', status: 'On Leave' },
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

export const GradebookPage = () => (
  <section>
    <PageHeader title='Gradebook' subtitle='Manage classes, subjects, and learner performance.' actionLabel='Add Grade Entry' />
    <article className='card'>
      <table className='table'>
        <thead>
          <tr>
            <th>Class</th>
            <th>Class Teacher</th>
            <th>Students</th>
            <th>Attendance</th>
          </tr>
        </thead>
        <tbody>
          {classes.map((row) => (
            <tr key={row.class}>
              <td>{row.class}</td>
              <td>{row.teacher}</td>
              <td>{row.students}</td>
              <td>{row.attendance}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </article>
  </section>
);

export const ExamsPage = () => (
  <section>
    <PageHeader title='Exams' subtitle='Plan assessments and publish exam schedules.' actionLabel='Create Exam Window' />
    <article className='card'>
      <table className='table'>
        <thead>
          <tr>
            <th>Subject</th>
            <th>Date</th>
            <th>Class</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {examSchedule.map((row) => (
            <tr key={`${row.subject}-${row.class}`}>
              <td>{row.subject}</td>
              <td>{row.date}</td>
              <td>{row.class}</td>
              <td>
                <span className='badge'>{row.status}</span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </article>
  </section>
);

export const ReportsPage = () => (
  <section>
    <PageHeader
      title='Reports'
      subtitle='View analytics and export school and class performance reports.'
      actionLabel='Export Reports'
    />
    <div className='card-grid card-grid-2'>
      <article className='card'>
        <h3>Performance Snapshot</h3>
        <p className='muted'>78% of learners are above target in core subjects this term.</p>
      </article>
      <article className='card'>
        <h3>Attendance Insights</h3>
        <p className='muted'>Average attendance improved by 3% from last term.</p>
      </article>
    </div>
  </section>
);

export const TeachersPage = () => (
  <section>
    <PageHeader title='Teachers' subtitle='Manage assignments, load, and teacher status.' actionLabel='Assign Teacher' />
    <article className='card'>
      <table className='table'>
        <thead>
          <tr>
            <th>Name</th>
            <th>Department</th>
            <th>Workload</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {teachers.map((row) => (
            <tr key={row.name}>
              <td>{row.name}</td>
              <td>{row.department}</td>
              <td>{row.workload}</td>
              <td>
                <span className='badge'>{row.status}</span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </article>
  </section>
);
