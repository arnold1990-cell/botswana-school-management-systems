import { createContext, useContext, useEffect, useState } from 'react';
import api, { ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY, clearStoredAuth, getAccessToken } from '../api/client';
import { User } from '../types/auth';

type Ctx = { user?: User; login: (email: string, password: string) => Promise<User>; logout: () => void };
type AuthContextValue = Ctx & { loading: boolean; authReady: boolean; isAuthenticated: boolean };
const AuthContext = createContext<AuthContextValue>({
  login: async () => { throw new Error('AuthProvider missing'); },
  logout: () => {},
  loading: true,
  authReady: false,
  isAuthenticated: false,
});
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

const hasValidToken = (token: string | null) => {
  if (!token) return false;
  const payload = parseJwtPayload(token);
  if (!payload?.exp) return true;
  return payload.exp * 1000 > Date.now();
};

type LoginResponseShape = {
  accessToken?: string;
  refreshToken?: string;
  user?: User;
};

const getLoginToken = (responseData: LoginResponseShape): string | null => {
  const raw = responseData.accessToken;
  return typeof raw === 'string' && raw.trim().length > 0 ? raw : null;
};

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<User>();
  const [loading, setLoading] = useState(true);
  const [authReady, setAuthReady] = useState(false);
  const [isAuthenticated, setIsAuthenticated] = useState(Boolean(getAccessToken()));

  const load = async (): Promise<User> => {
    const r = await api.get('/me');
    setUser(r.data);
    return r.data;
  };

  useEffect(() => {
    const bootstrap = async () => {
      const token = getAccessToken();
      if (import.meta.env.DEV) {
        console.info('[auth] bootstrap token read', { hasToken: Boolean(token) });
      }

      if (!hasValidToken(token)) {
        clearStoredAuth();
        setUser(undefined);
        setIsAuthenticated(false);
        setLoading(false);
        setAuthReady(true);
        return;
      }

      setIsAuthenticated(true);
      try {
        await load();
      } catch {
        clearStoredAuth();
        setUser(undefined);
        setIsAuthenticated(false);
      } finally {
        setLoading(false);
        setAuthReady(true);
      }
    };

    bootstrap();
  }, []);

  useEffect(() => {
    const syncAuthFromToken = () => {
      const token = getAccessToken();
      if (!token || !hasValidToken(token)) {
        setUser(undefined);
        setIsAuthenticated(false);
      }
    };

    const onStorage = (event: StorageEvent) => {
      if (event.key === ACCESS_TOKEN_KEY) {
        syncAuthFromToken();
      }
    };

    window.addEventListener('bosams:auth-cleared', syncAuthFromToken);
    window.addEventListener('storage', onStorage);

    return () => {
      window.removeEventListener('bosams:auth-cleared', syncAuthFromToken);
      window.removeEventListener('storage', onStorage);
    };
  }, []);

  const login = async (email: string, password: string): Promise<User> => {
    clearStoredAuth();
    sessionStorage.clear();

    const r = await api.post('/auth/login', { email, password });
    const loginResponse = (r.data ?? {}) as LoginResponseShape;
    const jwtValue = getLoginToken(loginResponse);
    if (jwtValue) {
      localStorage.setItem(ACCESS_TOKEN_KEY, jwtValue);
      setIsAuthenticated(true);
    } else {
      clearStoredAuth();
      setIsAuthenticated(false);
      throw new Error('Login response did not include an access token');
    }

    if (typeof loginResponse.refreshToken === 'string' && loginResponse.refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, loginResponse.refreshToken);
    }
    if (loginResponse.user) {
      setUser(loginResponse.user);
      return loginResponse.user;
    }

    return load();
  };

  const logout = () => {
    clearStoredAuth();
    sessionStorage.clear();
    setUser(undefined);
    setIsAuthenticated(false);
  };

  return <AuthContext.Provider value={{ user, login, logout, loading, authReady, isAuthenticated }}>{children}</AuthContext.Provider>;
};
