import { createContext, useContext, useEffect, useState } from 'react';
import api, { ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY, clearStoredAuth, getAccessToken } from '../api/client';
import { User } from '../types/auth';

type Ctx = { user?: User; login: (email: string, password: string) => Promise<User>; logout: () => void };
type AuthContextValue = Ctx & { loading: boolean };
const AuthContext = createContext<AuthContextValue>({ login: async () => { throw new Error('AuthProvider missing'); }, logout: () => {}, loading: true });
export const useAuth = () => useContext(AuthContext);

const parseJwtPayload = (token: string): { exp?: number } | null => {
  try {
    const [, payload] = token.split('.');
    if (!payload) return null;
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const json = decodeURIComponent(atob(base64).split('').map((char) => `%${(`00${char.charCodeAt(0).toString(16)}`).slice(-2)}`).join(''));
    return JSON.parse(json);
  } catch {
    return null;
  }
};

const hasValidToken = () => {
  const token = getAccessToken();
  if (!token) return false;
  const payload = parseJwtPayload(token);
  if (!payload?.exp) return true;
  return payload.exp * 1000 > Date.now();
};

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
      if (!hasValidToken()) {
        clearStoredAuth();
        setUser(undefined);
        setLoading(false);
        return;
      }

      try {
        await load();
      } catch {
        clearStoredAuth();
        setUser(undefined);
      } finally {
        setLoading(false);
      }
    };

    bootstrap();
  }, []);

  const login = async (email: string, password: string): Promise<User> => {
    const r = await api.post('/auth/login', { email, password });
    const jwtValue = typeof r.data?.accessToken === 'string' && r.data.accessToken ? r.data.accessToken : null;
    if (jwtValue) {
      localStorage.setItem('accessToken', jwtValue);
    } else {
      clearStoredAuth();
      throw new Error('Login response did not include an access token');
    }
    if (typeof r.data?.refreshToken === 'string' && r.data.refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, r.data.refreshToken);
    }
    if (import.meta.env.DEV) {
      console.info('[auth] after login: token stored', Boolean(localStorage.getItem(ACCESS_TOKEN_KEY)));
    }
    return load();
  };

  const logout = () => {
    clearStoredAuth();
    setUser(undefined);
  };

  return <AuthContext.Provider value={{ user, login, logout, loading }}>{children}</AuthContext.Provider>;
};
