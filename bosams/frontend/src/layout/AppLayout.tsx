import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export const AppLayout = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const navItems = user?.role === 'TEACHER'
    ? [
        { to: '/teacher/dashboard', label: 'Teacher Dashboard' },
        { to: '/subjects', label: 'Subjects' },
        { to: '/marks-entry', label: 'Marks Entry' },
        { to: '/reports', label: 'Reports' },
      ]
    : [
        { to: '/dashboard', label: 'Dashboard' },
        { to: '/learners', label: 'Learners' },
        { to: '/subjects', label: 'Subjects' },
        { to: '/marks-entry', label: 'Marks Entry' },
        { to: '/reports', label: 'Reports' },
        { to: '/teachers', label: 'Teachers' },
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
