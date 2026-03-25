import axios from 'axios';

export const ACCESS_TOKEN_KEY = 'accessToken';
export const REFRESH_TOKEN_KEY = 'bosams_refresh_token';
export const SESSION_EXPIRED_MESSAGE_KEY = 'bosams_session_expired_message';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

export const getAccessToken = (): string | null => localStorage.getItem(ACCESS_TOKEN_KEY);
export const getRefreshToken = (): string | null => localStorage.getItem(REFRESH_TOKEN_KEY);

export const clearStoredAuth = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
};

const clearAuthAndRedirect = () => {
  clearStoredAuth();
  if (typeof window !== 'undefined') {
    sessionStorage.setItem(SESSION_EXPIRED_MESSAGE_KEY, 'Your session expired. Please sign in again.');
    window.dispatchEvent(new Event('bosams:auth-cleared'));

    if (!window.location.pathname.startsWith('/login')) {
      window.location.href = '/login?sessionExpired=1';
    }
  }
};

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

type RetriableRequestConfig = {
  _retry?: boolean;
  _skipAuthRedirect?: boolean;
};

const refreshAccessToken = async (): Promise<string | null> => {
  const refreshToken = getRefreshToken();

  if (!refreshToken) {
    return null;
  }

  try {
    const response = await axios.post(`${API_BASE_URL}/auth/refresh`, { refreshToken }, {
      headers: {
        'Content-Type': 'application/json',
      },
    });

    const nextAccessToken = response.data?.accessToken;
    const nextRefreshToken = response.data?.refreshToken;
    if (typeof nextAccessToken !== 'string' || !nextAccessToken) {
      return null;
    }

    localStorage.setItem(ACCESS_TOKEN_KEY, nextAccessToken);
    if (typeof nextRefreshToken === 'string' && nextRefreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, nextRefreshToken);
    }
    return nextAccessToken;
  } catch {
    return null;
  }
};

api.interceptors.request.use((config) => {
  const token = getAccessToken();

  config.headers = config.headers || {};
  config.headers['Content-Type'] = 'application/json';

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  const authHeaderAttached = Boolean(config.headers.Authorization);
  if (import.meta.env.DEV) console.info('[api] auth request', { hasAccessToken: Boolean(token), authHeaderAttached });

  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const status = error.response?.status;
    const requestUrl = `${error.config?.baseURL ?? ''}${error.config?.url ?? ''}`;
    const originalRequest = error.config as typeof error.config & RetriableRequestConfig;

    if (status === 401 && originalRequest && !originalRequest._skipAuthRedirect && !originalRequest._retry && !requestUrl.endsWith('/auth/login') && !requestUrl.endsWith('/auth/refresh')) {
      originalRequest._retry = true;
      const nextAccessToken = await refreshAccessToken();

      if (nextAccessToken) {
        originalRequest.headers = originalRequest.headers || {};
        originalRequest.headers.Authorization = `Bearer ${nextAccessToken}`;
        return api(originalRequest);
      }

      clearAuthAndRedirect();
    }

    if (status === 401 && (!originalRequest || (!originalRequest._skipAuthRedirect && (requestUrl.endsWith('/auth/login') || requestUrl.endsWith('/auth/refresh'))))) {
      clearAuthAndRedirect();
    }

    return Promise.reject(error);
  }
);

export default api;
