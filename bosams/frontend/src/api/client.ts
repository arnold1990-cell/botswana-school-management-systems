import axios from 'axios';

export const ACCESS_TOKEN_KEY = 'accessToken';
export const REFRESH_TOKEN_KEY = 'bosams_refresh_token';
const LEGACY_ACCESS_TOKEN_KEYS = ['token', 'jwt', 'authToken', 'session token'];
const LEGACY_REFRESH_TOKEN_KEYS = ['refreshToken'];

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';
const allAccessTokenKeys = [ACCESS_TOKEN_KEY, ...LEGACY_ACCESS_TOKEN_KEYS];
const allRefreshTokenKeys = [REFRESH_TOKEN_KEY, ...LEGACY_REFRESH_TOKEN_KEYS];

const migrateLegacyToken = (primaryKey: string, legacyKeys: string[] = []) => {
  for (const key of legacyKeys) {
    const legacy = localStorage.getItem(key);
    if (legacy) {
      localStorage.setItem(primaryKey, legacy);
      localStorage.removeItem(key);
      return;
    }
  }
};

migrateLegacyToken(ACCESS_TOKEN_KEY, LEGACY_ACCESS_TOKEN_KEYS);
migrateLegacyToken(REFRESH_TOKEN_KEY, LEGACY_REFRESH_TOKEN_KEYS);

export const getAccessToken = (): string | null => localStorage.getItem(ACCESS_TOKEN_KEY);
export const getRefreshToken = (): string | null => localStorage.getItem(REFRESH_TOKEN_KEY);

const clearStoredKeys = (keys: string[]) => {
  keys.forEach((key) => localStorage.removeItem(key));
};

export const clearStoredAuth = () => {
  clearStoredKeys(allAccessTokenKeys);
  clearStoredKeys(allRefreshTokenKeys);
};

const clearAuthAndRedirect = () => {
  clearStoredAuth();
  window.location.href = '/login';
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
    if (import.meta.env.DEV) {
      console.info('[api] refresh skipped', { reason: 'missing_refresh_token' });
    }
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
  const requestUrl = `${config.baseURL ?? ''}${config.url ?? ''}`;
  config.headers = config.headers || {};
  config.headers['Content-Type'] = 'application/json';

  let attachedAuthorization = false;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
    attachedAuthorization = true;
  }

  if (import.meta.env.DEV) {
    console.info('[api] before request token read', {
      tokenKey: ACCESS_TOKEN_KEY,
      hasToken: Boolean(token),
    });
    console.info('[api] before request Authorization header attached', attachedAuthorization);
    console.info('[api] request', { url: requestUrl });
  }
  return config;
});

api.interceptors.response.use(
  (response) => {
    const requestUrl = `${response.config.baseURL ?? ''}${response.config.url ?? ''}`;
    if (import.meta.env.DEV) {
      console.info('[api] response', { url: requestUrl, status: response.status });
    }
    return response;
  },
  async (error) => {
    const status = error.response?.status;
    const requestUrl = `${error.config?.baseURL ?? ''}${error.config?.url ?? ''}`;
    const originalRequest = error.config as typeof error.config & RetriableRequestConfig;
    if (import.meta.env.DEV) {
      console.info('[api] response', { url: requestUrl, status: status ?? 0 });
    }

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
