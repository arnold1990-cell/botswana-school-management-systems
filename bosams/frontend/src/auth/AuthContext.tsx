import { createContext, useContext, useEffect, useState } from 'react';
import api, { ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY, clearStoredAuth, getAccessToken } from '../api/client';
import { User } from '../types/auth';

type Ctx = { user?: User; login: (email: string, password: string) => Promise<User>; logout: () => void };
type AuthContextValue = Ctx & { loading: boolean; authInitialized: boolean; isAuthenticated: boolean };
const AuthContext = createContext<AuthContextValue>({
  login: async () => { throw new Error('AuthProvider missing'); },
  logout: () => {},
  loading: true,
  authInitialized: false,
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

const hasValidToken = () => {
  const token = getAccessToken();
  if (!token) return false;
  const payload = parseJwtPayload(token);
  if (!payload?.exp) return true;
  return payload.exp * 1000 > Date.now();
};

type LoginResponseShape = {
  accessToken?: string;
  token?: string;
  jwt?: string;
  refreshToken?: string;
  user?: User;
};

const getLoginToken = (responseData: LoginResponseShape): string | null => {
  const raw = responseData.accessToken ?? responseData.token ?? responseData.jwt;
  return typeof raw === 'string' && raw.trim().length > 0 ? raw : null;
};

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<User>();
  const [loading, setLoading] = useState(true);
  const [authInitialized, setAuthInitialized] = useState(false);
  const load = async (): Promise<User> => {
    const r = await api.get('/me');
    setUser(r.data);
    return r.data;
  };

  useEffect(() => {
    const bootstrap = async () => {
      if (import.meta.env.DEV) {
        console.info('[auth] bootstrap: token read', {
          tokenKey: ACCESS_TOKEN_KEY,
          hasToken: Boolean(getAccessToken()),
        });
      }
      if (!hasValidToken()) {
        clearStoredAuth();
        setUser(undefined);
        setLoading(false);
        setAuthInitialized(true);
        return;
      }

      try {
        await load();
      } catch {
        clearStoredAuth();
        setUser(undefined);
      } finally {
        setLoading(false);
        setAuthInitialized(true);
      }
    };

    bootstrap();
  }, []);

  const login = async (email: string, password: string): Promise<User> => {
    const r = await api.post('/auth/login', { email, password });
    const loginResponse = (r.data ?? {}) as LoginResponseShape;
    if (import.meta.env.DEV) {
      console.info('[auth] login response fields', {
        hasAccessToken: Boolean(loginResponse.accessToken),
        hasToken: Boolean(loginResponse.token),
        hasJwt: Boolean(loginResponse.jwt),
        hasRefreshToken: Boolean(loginResponse.refreshToken),
        hasUser: Boolean(loginResponse.user),
      });
    }
    const jwtValue = getLoginToken(loginResponse);
    if (jwtValue) {
      localStorage.setItem(ACCESS_TOKEN_KEY, jwtValue);
    } else {
      clearStoredAuth();
      throw new Error('Login response did not include an access token');
    }
    if (typeof loginResponse.refreshToken === 'string' && loginResponse.refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, loginResponse.refreshToken);
    }
    if (import.meta.env.DEV) {
      console.info('[auth] after login: token stored', {
        tokenKey: ACCESS_TOKEN_KEY,
        hasToken: Boolean(localStorage.getItem(ACCESS_TOKEN_KEY)),
      });
    }
    if (loginResponse.user) {
      setUser(loginResponse.user);
      return loginResponse.user;
    }

    return load();
  };

  const logout = () => {
    clearStoredAuth();
    setUser(undefined);
  };

  return <AuthContext.Provider value={{ user, login, logout, loading, authInitialized, isAuthenticated: Boolean(user) }}>{children}</AuthContext.Provider>;
};
