import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { Role } from '../types/auth';

export const RoleProtectedRoute = ({ allowedRoles }: { allowedRoles: Role[] }) => {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!user) {
    return <Navigate to='/login' replace state={{ from: location }} />;
  }

  if (!allowedRoles.includes(user.role)) {
    const fallbackRoute = user.role === 'TEACHER' ? '/teacher/dashboard' : '/dashboard';
    return <Navigate to={fallbackRoute} replace />;
  }

  return <Outlet />;
};
