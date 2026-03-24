import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { getAccessToken } from '../api/client';
import { useAuth } from '../auth/AuthContext';

export const ProtectedRoute = () => {
  const location = useLocation();
  const { loading, authInitialized, isAuthenticated } = useAuth();
  const accessToken = getAccessToken();

  if (!authInitialized || loading) {
    return <div>Loading...</div>;
  }

  if (!accessToken || !isAuthenticated) {
    return <Navigate to='/login' replace state={{ from: location }} />;
  }

  return <Outlet />;
};
