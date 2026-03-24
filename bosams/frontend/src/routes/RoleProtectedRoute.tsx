import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { getAccessToken } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { Role } from '../types/auth';

export const RoleProtectedRoute = ({ allowedRoles }: { allowedRoles: Role[] }) => {
  const { user, loading, authInitialized, isAuthenticated } = useAuth();
  const accessToken = getAccessToken();
  const location = useLocation();

  if (!authInitialized || loading) {
    return <div>Loading...</div>;
  }

  if (!accessToken || !isAuthenticated || !user) {
    return <Navigate to='/login' replace state={{ from: location }} />;
  }

  if (!allowedRoles.includes(user.role)) {
    const fallbackRoute = user.role === 'TEACHER'
      ? '/teacher/dashboard'
      : user.role === 'STUDENT'
        ? '/student/dashboard'
        : user.role === 'PARENT'
          ? '/parent/dashboard'
          : user.role === 'ACCOUNTANT'
            ? '/accountant/dashboard'
            : '/dashboard';
    return <Navigate to={fallbackRoute} replace />;
  }

  return <Outlet />;
};
