import { Navigate, Outlet } from 'react-router-dom';
import { ACCESS_TOKEN_KEY } from '../api/http';

export const ProtectedRoute = () => {
  const accessToken = localStorage.getItem(ACCESS_TOKEN_KEY);

  if (!accessToken) {
    return <Navigate to='/login' replace />;
  }

  return <Outlet />;
};
