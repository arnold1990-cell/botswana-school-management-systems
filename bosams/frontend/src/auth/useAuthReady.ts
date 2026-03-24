import { useAuth } from './AuthContext';

export const useAuthReady = () => {
  const { loading, user } = useAuth();
  const authReady = !loading && Boolean(user);

  return { authReady, authLoading: loading, user };
};
