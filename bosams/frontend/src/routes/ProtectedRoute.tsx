import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export const ProtectedRoute = () => {
  const location = useLocation();
  const { loading, authReady, isAuthenticated } = useAuth();

  if (!authReady || loading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to='/login' replace state={{ from: location }} />;
  }

  return <Outlet />;
};
