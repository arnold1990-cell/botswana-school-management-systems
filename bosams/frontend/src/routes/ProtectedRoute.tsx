import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { getAccessToken } from '../api/client';
import { useAuth } from '../auth/AuthContext';

export const ProtectedRoute = () => {
  const location = useLocation();
  const { loading, user } = useAuth();
  const accessToken = getAccessToken();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!accessToken || !user) {
    return <Navigate to='/login' replace state={{ from: location }} />;
  }

  return <Outlet />;
};
