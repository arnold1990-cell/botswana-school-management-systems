import { createContext, useContext, useEffect, useState } from 'react';
import api, { ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY, getAccessToken } from '../api/client';
import { User } from '../types/auth';

type Ctx = { user?: User; login: (email: string, password: string) => Promise<User>; logout: () => void };
type AuthContextValue = Ctx & { loading: boolean };
const AuthContext = createContext<AuthContextValue>({ login: async () => { throw new Error('AuthProvider missing'); }, logout: () => {}, loading: true });
export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<User>();
  const [loading, setLoading] = useState(true);

  const load = async (): Promise<User> => {
    const r = await api.get('/me');
    setUser(r.data);
    return r.data;
  };

  useEffect(() => {
    const bootstrap = async () => {
      if (!getAccessToken()) {
        setLoading(false);
        return;
      }

      try {
        await load();
      } catch {
        setUser(undefined);
      } finally {
        setLoading(false);
      }
    };

    bootstrap();
  }, []);

  const login = async (email: string, password: string): Promise<User> => {
    const r = await api.post('/auth/login', { email, password });
    localStorage.setItem(ACCESS_TOKEN_KEY, r.data.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, r.data.refreshToken);
    return load();
  };

  const logout = () => {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    setUser(undefined);
  };

  return <AuthContext.Provider value={{ user, login, logout, loading }}>{children}</AuthContext.Provider>;
};
