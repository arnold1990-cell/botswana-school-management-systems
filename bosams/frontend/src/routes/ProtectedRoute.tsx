import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { getAccessToken } from '../api/client';

export const ProtectedRoute = () => {
  const location = useLocation();
  const accessToken = getAccessToken();

  if (!accessToken) {
    return <Navigate to='/login' replace state={{ from: location }} />;
  }

  return <Outlet />;
};
