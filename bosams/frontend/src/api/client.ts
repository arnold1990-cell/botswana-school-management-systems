import axios from 'axios';

export const ACCESS_TOKEN_KEY = 'bosams_access_token';
export const REFRESH_TOKEN_KEY = 'bosams_refresh_token';
const LEGACY_ACCESS_TOKEN_KEYS = ['accessToken', 'token'];
const LEGACY_REFRESH_TOKEN_KEYS = ['refreshToken'];

type BrowserStorage = Pick<Storage, 'getItem' | 'setItem' | 'removeItem'>;
type StorageType = 'localStorage' | 'sessionStorage';
type StorageLookup = { storage: BrowserStorage; storageType: StorageType };
type TokenLookup = { token: string | null; key: string | null; storageType: StorageType | null };

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

const storageLookups: StorageLookup[] = [
  { storage: localStorage, storageType: 'localStorage' },
  { storage: sessionStorage, storageType: 'sessionStorage' },
];

const allAccessTokenKeys = [ACCESS_TOKEN_KEY, ...LEGACY_ACCESS_TOKEN_KEYS];
const allRefreshTokenKeys = [REFRESH_TOKEN_KEY, ...LEGACY_REFRESH_TOKEN_KEYS];

const readStoredToken = (primaryKey: string, legacyKeys: string[] = []): TokenLookup => {
  for (const { storage, storageType } of storageLookups) {
    const primary = storage.getItem(primaryKey);
    if (primary) {
      return { token: primary, key: primaryKey, storageType };
    }
  }

  for (const key of legacyKeys) {
    for (const { storage, storageType } of storageLookups) {
      const legacy = storage.getItem(key);
      if (legacy) {
        // Migrate legacy token to the canonical key in localStorage.
        localStorage.setItem(primaryKey, legacy);
        storage.removeItem(key);
        return { token: legacy, key, storageType };
      }
    }
  }

  return { token: null, key: null, storageType: null };
};

export const getAccessToken = (): string | null => readStoredToken(ACCESS_TOKEN_KEY, LEGACY_ACCESS_TOKEN_KEYS).token;
export const getRefreshToken = (): string | null => readStoredToken(REFRESH_TOKEN_KEY, LEGACY_REFRESH_TOKEN_KEYS).token;

const clearStoredKeys = (keys: string[]) => {
  for (const { storage } of storageLookups) {
    keys.forEach((key) => storage.removeItem(key));
  }
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
  const refreshTokenInfo = readStoredToken(REFRESH_TOKEN_KEY, LEGACY_REFRESH_TOKEN_KEYS);

  if (!refreshTokenInfo.token) {
    if (import.meta.env.DEV) {
      console.info('[api] refresh skipped', { reason: 'missing_refresh_token' });
    }
    return null;
  }

  try {
    const response = await axios.post(`${API_BASE_URL}/auth/refresh`, { refreshToken: refreshTokenInfo.token }, {
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
  const tokenInfo = readStoredToken(ACCESS_TOKEN_KEY, LEGACY_ACCESS_TOKEN_KEYS);
  const requestUrl = `${config.baseURL ?? ''}${config.url ?? ''}`;
  config.headers = config.headers || {};
  config.headers['Content-Type'] = 'application/json';

  let attachedAuthorization = false;
  if (tokenInfo.token) {
    config.headers.Authorization = `Bearer ${tokenInfo.token}`;
    attachedAuthorization = true;
  }

  console.info('[api] request', {
    url: requestUrl,
    hasToken: Boolean(tokenInfo.token),
    tokenStorageKey: tokenInfo.key,
    tokenStorageType: tokenInfo.storageType,
    attachedAuthorization,
  });
  return config;
});

api.interceptors.response.use(
  (response) => {
    const requestUrl = `${response.config.baseURL ?? ''}${response.config.url ?? ''}`;
    console.info('[api] response', { url: requestUrl, status: response.status });
    return response;
  },
  async (error) => {
    const status = error.response?.status;
    const requestUrl = `${error.config?.baseURL ?? ''}${error.config?.url ?? ''}`;
    const tokenInfo = readStoredToken(ACCESS_TOKEN_KEY, LEGACY_ACCESS_TOKEN_KEYS);
    const originalRequest = error.config as typeof error.config & RetriableRequestConfig;
    console.info('[api] response', {
      url: requestUrl,
      status: status ?? 0,
      hasToken: Boolean(tokenInfo.token),
      tokenStorageKey: tokenInfo.key,
      tokenStorageType: tokenInfo.storageType,
    });

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
