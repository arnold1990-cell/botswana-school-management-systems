import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export const AppLayout = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const navItems = user?.role === 'TEACHER'
    ? [
      { to: '/teacher/dashboard', label: 'Teacher Dashboard' },
      { to: '/learners', label: 'Learners' },
      { to: '/subjects', label: 'Subjects' },
      { to: '/marks-entry', label: 'Marks Entry' },
      { to: '/reports', label: 'Reports' },
      { to: '/attendance', label: 'Attendance' },
      { to: '/announcements', label: 'Announcements' },
      { to: '/timetable', label: 'Timetable' },
      { to: '/assignments', label: 'Assignments' },
    ]
    : user?.role === 'STUDENT'
      ? [
        { to: '/student/dashboard', label: 'Student Dashboard' },
        { to: '/announcements', label: 'Announcements' },
        { to: '/attendance', label: 'Attendance' },
        { to: '/timetable', label: 'Timetable' },
        { to: '/assignments', label: 'Assignments' },
      ]
      : user?.role === 'PARENT'
        ? [
          { to: '/parent/dashboard', label: 'Parent Dashboard' },
          { to: '/announcements', label: 'Announcements' },
          { to: '/attendance', label: 'Attendance' },
          { to: '/timetable', label: 'Timetable' },
          { to: '/fees', label: 'Fees' },
        ]
        : user?.role === 'ACCOUNTANT'
          ? [
            { to: '/accountant/dashboard', label: 'Accountant Dashboard' },
            { to: '/fees', label: 'Fees' },
            { to: '/reports', label: 'Reports' },
            { to: '/announcements', label: 'Announcements' },
          ]
          : [
            { to: '/dashboard', label: 'Dashboard' },
            { to: '/learners', label: 'Learners' },
            { to: '/subjects', label: 'Subjects' },
            { to: '/marks-entry', label: 'Marks Entry' },
            { to: '/reports', label: 'Reports' },
            { to: '/teachers', label: 'Teachers' },
            { to: '/attendance', label: 'Attendance' },
            { to: '/announcements', label: 'Announcements' },
            { to: '/timetable', label: 'Timetable' },
            { to: '/assignments', label: 'Assignments' },
            { to: '/exams', label: 'Exams' },
            { to: '/fees', label: 'Fees' },
            { to: '/library', label: 'Library' },
            { to: '/holidays', label: 'Holidays' },
            { to: '/messages', label: 'Messages' },
            { to: '/settings', label: 'Settings' },
          ];

  const onLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <div className='app-shell'>
      <aside className='app-sidebar'>
        <div className='brand'>
          <span className='brand-dot' />
          BOSAMS
        </div>
        <nav className='app-nav'>
          {navItems.map((item) => (
            <NavLink key={item.to} to={item.to} className={({ isActive }) => `app-nav-link ${isActive ? 'active' : ''}`}>
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <div className='app-main'>
        <header className='app-topbar'>
          <div>
            <p className='topbar-eyebrow'>Botswana School Management System</p>
            <h1 className='topbar-title'>School Operations Console</h1>
          </div>
          <div className='topbar-user'>
            <span>{user?.fullName ?? 'School Admin'}</span>
            <button className='btn btn-secondary' type='button' onClick={onLogout}>Logout</button>
          </div>
        </header>

        <main className='app-content'>
          <Outlet />
        </main>
      </div>
    </div>
  );
};
