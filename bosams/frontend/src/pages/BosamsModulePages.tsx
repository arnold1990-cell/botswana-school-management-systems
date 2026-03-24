import { FormEvent, useEffect, useMemo, useState } from 'react';
import api from '../api/client';
import { useAuth } from '../auth/AuthContext';

type Announcement = {
  id: number;
  title: string;
  message: string;
  targetRole?: string;
  targetGradeLevel?: number;
  createdAt: string;
  expiresAt?: string;
};

type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE' | 'HALF_DAY';

type AttendanceRecord = {
  id: number;
  attendanceDate: string;
  studentId: number;
  studentName: string;
  gradeLevel: number;
  status: AttendanceStatus;
  remark?: string;
};

const ModulePage = ({ title, description }: { title: string; description: string }) => (
  <section>
    <div className='page-header'>
      <div>
        <h2>{title}</h2>
        <p className='muted'>{description}</p>
      </div>
    </div>
    <article className='card'>
      <p className='muted'>This BOSAMS module is scaffolded and ready for extension from this page.</p>
    </article>
  </section>
);

export const AttendancePage = () => {
  const { user } = useAuth();
  const [gradeLevel, setGradeLevel] = useState(1);
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const [records, setRecords] = useState<AttendanceRecord[]>([]);
  const [studentId, setStudentId] = useState('');
  const [status, setStatus] = useState<AttendanceStatus>('PRESENT');
  const [remark, setRemark] = useState('');

  const canMark = useMemo(() => user?.role === 'ADMIN' || user?.role === 'PRINCIPAL' || user?.role === 'TEACHER', [user?.role]);

  const load = async () => {
    const response = await api.get<AttendanceRecord[]>(`/attendance/grade/${gradeLevel}`, { params: { date } });
    setRecords(response.data);
  };

  useEffect(() => {
    if (canMark) {
      load();
    }
  }, [canMark, gradeLevel, date]);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    const parsedStudentId = Number(studentId);
    if (!parsedStudentId) {
      return;
    }
    await api.post('/attendance/mark', {
      gradeLevel,
      attendanceDate: date,
      items: [{ studentId: parsedStudentId, status, remark }],
    });
    setStudentId('');
    setRemark('');
    await load();
  };

  return (
    <section>
      <div className='page-header'>
        <div>
          <h2>Attendance</h2>
          <p className='muted'>Mark and review daily attendance by grade.</p>
        </div>
      </div>

      <article className='card'>
        <div className='card-grid card-grid-2'>
          <label>Grade <input type='number' min={1} value={gradeLevel} onChange={(e) => setGradeLevel(Number(e.target.value || '1'))} /></label>
          <label>Date <input type='date' value={date} onChange={(e) => setDate(e.target.value)} /></label>
        </div>
      </article>

      {canMark && (
        <article className='card' style={{ marginTop: 12 }}>
          <h3>Quick Mark</h3>
          <form className='card-grid card-grid-2' onSubmit={submit}>
            <label>Student ID <input value={studentId} onChange={(e) => setStudentId(e.target.value)} required /></label>
            <label>Status
              <select value={status} onChange={(e) => setStatus(e.target.value as AttendanceStatus)}>
                <option value='PRESENT'>Present</option>
                <option value='ABSENT'>Absent</option>
                <option value='LATE'>Late</option>
                <option value='HALF_DAY'>Half Day</option>
              </select>
            </label>
            <label>Remark <input value={remark} onChange={(e) => setRemark(e.target.value)} /></label>
            <button className='btn btn-primary' type='submit'>Save Attendance</button>
          </form>
        </article>
      )}

      <article className='card' style={{ marginTop: 12 }}>
        <h3>Attendance Register</h3>
        <table className='table'>
          <thead><tr><th>Date</th><th>Student</th><th>Status</th><th>Remark</th></tr></thead>
          <tbody>
            {records.map((record) => (
              <tr key={record.id}>
                <td>{record.attendanceDate}</td>
                <td>{record.studentName}</td>
                <td><span className='badge'>{record.status}</span></td>
                <td>{record.remark || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </article>
    </section>
  );
};

export const AnnouncementsPage = () => {
  const { user } = useAuth();
  const [items, setItems] = useState<Announcement[]>([]);
  const [title, setTitle] = useState('');
  const [message, setMessage] = useState('');
  const [targetRole, setTargetRole] = useState('');

  const canPublish = user?.role === 'ADMIN' || user?.role === 'PRINCIPAL' || user?.role === 'TEACHER';

  const load = async () => {
    const response = await api.get<Announcement[]>('/announcements');
    setItems(response.data);
  };

  useEffect(() => { load(); }, []);

  const publish = async (event: FormEvent) => {
    event.preventDefault();
    await api.post('/announcements', {
      title,
      message,
      targetRole: targetRole || null,
    });
    setTitle('');
    setMessage('');
    setTargetRole('');
    await load();
  };

  return (
    <section>
      <div className='page-header'>
        <div>
          <h2>Announcements</h2>
          <p className='muted'>School-wide and role-targeted notices.</p>
        </div>
      </div>

      {canPublish && (
        <article className='card'>
          <h3>Publish Announcement</h3>
          <form className='card-grid card-grid-2' onSubmit={publish}>
            <label>Title <input value={title} onChange={(e) => setTitle(e.target.value)} required /></label>
            <label>Target Role
              <select value={targetRole} onChange={(e) => setTargetRole(e.target.value)}>
                <option value=''>All Roles</option>
                <option value='TEACHER'>Teacher</option>
                <option value='STUDENT'>Student</option>
                <option value='PARENT'>Parent</option>
                <option value='ACCOUNTANT'>Accountant</option>
              </select>
            </label>
            <label style={{ gridColumn: '1 / -1' }}>Message <input value={message} onChange={(e) => setMessage(e.target.value)} required /></label>
            <button className='btn btn-primary' type='submit'>Post Announcement</button>
          </form>
        </article>
      )}

      <article className='card' style={{ marginTop: 12 }}>
        <h3>Latest Notices</h3>
        <table className='table'>
          <thead><tr><th>Title</th><th>Target</th><th>Published</th></tr></thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td>{item.title}<br /><span className='muted'>{item.message}</span></td>
                <td>{item.targetRole || 'ALL'}</td>
                <td>{new Date(item.createdAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </article>
    </section>
  );
};

export const TimetablePage = () => <ModulePage title='Timetable' description='Class, teacher, and learner timetable view.' />;
export const AssignmentsPage = () => <ModulePage title='Assignments' description='Create tasks, collect submissions, and review feedback.' />;
export const ExamsPage = () => <ModulePage title='Exams & Results' description='Manage exams, results, and student performance summaries.' />;
export const FeesPage = () => <ModulePage title='Fees & Finance' description='Session-year fees setup, payments, receipts, and balances.' />;
export const LibraryPage = () => <ModulePage title='Library' description='Digital resources, uploads, and search-ready listings.' />;
export const HolidaysPage = () => <ModulePage title='Holiday List' description='School calendar and holiday configuration.' />;
export const MessagesPage = () => <ModulePage title='Messages & Notifications' description='In-app communications and notification center.' />;
export const SettingsPage = () => <ModulePage title='Settings' description='School profile, configuration, and security settings.' />;
