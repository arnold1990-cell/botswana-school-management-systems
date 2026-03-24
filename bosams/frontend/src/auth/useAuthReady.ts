import { useAuth } from './AuthContext';

export const useAuthReady = () => {
  const { loading, user, authInitialized, isAuthenticated } = useAuth();
  const authReady = authInitialized && !loading;

  return { authReady, authLoading: loading, user, isAuthenticated };
};
