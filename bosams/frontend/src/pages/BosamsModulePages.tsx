import { FormEvent, useEffect, useMemo, useState } from 'react';
import api from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { useAuthReady } from '../auth/useAuthReady';

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

type ModulePageProps = {
  title: string;
  description: string;
  summaryLabel: string;
  summaryValue: string;
  primaryAction: string;
  placeholderItems: string[];
};

const ModulePage = ({
  title,
  description,
  summaryLabel,
  summaryValue,
  primaryAction,
  placeholderItems,
}: ModulePageProps) => (
  <section>
    <div className='page-header'>
      <div>
        <h2>{title}</h2>
        <p className='muted'>{description}</p>
      </div>
      <button className='btn btn-primary' type='button'>{primaryAction}</button>
    </div>

    <div className='card-grid card-grid-2'>
      <article className='card metric-card'>
        <p className='muted'>{summaryLabel}</p>
        <h3>{summaryValue}</h3>
        <span className='badge'>Ready</span>
      </article>
      <article className='card'>
        <h3>Quick Summary</h3>
        <p className='muted'>Use this area to manage and review records for the {title.toLowerCase()} module.</p>
      </article>
    </div>

    <article className='card' style={{ marginTop: 12 }}>
      <h3>{title} Overview</h3>
      <table className='table'>
        <thead><tr><th>Item</th><th>Status</th></tr></thead>
        <tbody>
          {placeholderItems.map((item) => (
            <tr key={item}>
              <td>{item}</td>
              <td><span className='badge'>Active</span></td>
            </tr>
          ))}
        </tbody>
      </table>
    </article>
  </section>
);

export const AttendancePage = () => {
  const { user } = useAuth();
  const { authReady, isAuthenticated } = useAuthReady();
  const [gradeLevel, setGradeLevel] = useState(1);
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const [records, setRecords] = useState<AttendanceRecord[]>([]);
  const [studentId, setStudentId] = useState('');
  const [status, setStatus] = useState<AttendanceStatus>('PRESENT');
  const [remark, setRemark] = useState('');
  const [error, setError] = useState('');

  const canMark = useMemo(() => user?.role === 'ADMIN' || user?.role === 'PRINCIPAL' || user?.role === 'TEACHER', [user?.role]);

  const load = async () => {
    try {
      setError('');
      const response = await api.get<AttendanceRecord[]>(`/attendance/grade/${gradeLevel}`, { params: { date } });
      setRecords(response.data ?? []);
    } catch {
      setRecords([]);
      setError('Attendance data is temporarily unavailable. You can still use this page to mark attendance.');
    }
  };

  useEffect(() => {
    if (authReady && isAuthenticated && canMark) {
      load();
    }
  }, [authReady, isAuthenticated, canMark, gradeLevel, date]);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    const parsedStudentId = Number(studentId);
    if (!parsedStudentId) {
      return;
    }
    try {
      setError('');
      await api.post('/attendance/mark', {
        gradeLevel,
        attendanceDate: date,
        items: [{ studentId: parsedStudentId, status, remark }],
      });
      setStudentId('');
      setRemark('');
      await load();
    } catch {
      setError('Unable to save attendance right now. Please try again.');
    }
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
        {error && <p className='muted' style={{ marginBottom: 10 }}>{error}</p>}
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
  const { authReady, isAuthenticated } = useAuthReady();
  const [items, setItems] = useState<Announcement[]>([]);
  const [title, setTitle] = useState('');
  const [message, setMessage] = useState('');
  const [targetRole, setTargetRole] = useState('');
  const [error, setError] = useState('');

  const canPublish = user?.role === 'ADMIN' || user?.role === 'PRINCIPAL' || user?.role === 'TEACHER';

  const load = async () => {
    try {
      setError('');
      const response = await api.get<Announcement[]>('/announcements');
      setItems(response.data ?? []);
    } catch {
      setItems([]);
      setError('Announcements could not be loaded at the moment.');
    }
  };

  useEffect(() => {
    if (!authReady || !isAuthenticated) return;
    load();
  }, [authReady, isAuthenticated]);

  const publish = async (event: FormEvent) => {
    event.preventDefault();
    try {
      setError('');
      await api.post('/announcements', {
        title,
        message,
        targetRole: targetRole || null,
      });
      setTitle('');
      setMessage('');
      setTargetRole('');
      await load();
    } catch {
      setError('Could not publish announcement. Please try again.');
    }
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
        {error && <p className='muted' style={{ marginBottom: 10 }}>{error}</p>}
        <table className='table'>
          <thead><tr><th>Title</th><th>Target</th><th>Published</th></tr></thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td>{item.title}<br /><span className='muted'>{item.message}</span></td>
                <td>{item.targetRole || 'ALL'}</td>
                <td>{item.createdAt ? new Date(item.createdAt).toLocaleString() : '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </article>
    </section>
  );
};

export const TimetablePage = () => <ModulePage title='Timetable' description='Class, teacher, and learner timetable view.' summaryLabel='Today Classes' summaryValue='8' primaryAction='Manage Timetable' placeholderItems={['Form 1A Morning Slot', 'Form 2B Lab Session', 'Teacher Coverage']} />;
export const ExamsPage = () => <ModulePage title='Exams & Results' description='Manage exams, results, and student performance summaries.' summaryLabel='Scheduled Exams' summaryValue='6' primaryAction='Schedule Exam' placeholderItems={['Mid-Term Timetable', 'Result Publishing Queue', 'Exam Moderation Checklist']} />;
export const FeesPage = () => <ModulePage title='Fees & Finance' description='Session-year fees setup, payments, receipts, and balances.' summaryLabel='Outstanding Balance' summaryValue='BWP 24,800' primaryAction='Record Payment' placeholderItems={['Pending Parent Payments', 'Recent Receipts', 'Fee Structure by Grade']} />;
export const LibraryPage = () => <ModulePage title='Library' description='Digital resources, uploads, and search-ready listings.' summaryLabel='Books Available' summaryValue='1,248' primaryAction='Add Resource' placeholderItems={['New Arrivals', 'Borrowed Items', 'Overdue Returns']} />;
export const HolidaysPage = () => <ModulePage title='Holiday List' description='School calendar and holiday configuration.' summaryLabel='Upcoming Holidays' summaryValue='4' primaryAction='Add Holiday' placeholderItems={['Mid-Term Break', 'National Holiday Calendar', 'School Event Closures']} />;
export const MessagesPage = () => <ModulePage title='Messages & Notifications' description='In-app communications and notification center.' summaryLabel='Unread Messages' summaryValue='9' primaryAction='Compose Message' placeholderItems={['Parent Broadcast', 'Teacher Circular', 'Student Notifications']} />;
export const SettingsPage = () => <ModulePage title='Settings' description='School profile, configuration, and security settings.' summaryLabel='Configuration Areas' summaryValue='7' primaryAction='Update Settings' placeholderItems={['School Profile', 'Academic Year Setup', 'Access & Security']} />;
