import { useAuth } from './AuthContext';

export const useAuthReady = () => {
  const { loading, user, authReady, isAuthenticated } = useAuth();

  return { authReady, authLoading: loading, user, isAuthenticated };
};
