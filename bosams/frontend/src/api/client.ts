import axios from 'axios';

export const ACCESS_TOKEN_KEY = 'bosams_access_token';
export const REFRESH_TOKEN_KEY = 'bosams_refresh_token';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

const clearAuthAndRedirect = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
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
};

const refreshAccessToken = async (): Promise<string | null> => {
  const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);

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
    if (typeof nextAccessToken !== 'string' || !nextAccessToken) {
      return null;
    }

    localStorage.setItem(ACCESS_TOKEN_KEY, nextAccessToken);
    return nextAccessToken;
  } catch {
    return null;
  }
};

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(ACCESS_TOKEN_KEY);
  const requestUrl = `${config.baseURL ?? ''}${config.url ?? ''}`;
  config.headers = config.headers || {};
  config.headers['Content-Type'] = 'application/json';

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  console.info('[api] request', { url: requestUrl, hasToken: Boolean(token) });
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
    const token = localStorage.getItem(ACCESS_TOKEN_KEY);
    const originalRequest = error.config as typeof error.config & RetriableRequestConfig;
    console.info('[api] response', { url: requestUrl, hasToken: Boolean(token), status: status ?? 0 });

    if (status === 401 && originalRequest && !originalRequest._retry && !requestUrl.endsWith('/auth/login') && !requestUrl.endsWith('/auth/refresh')) {
      originalRequest._retry = true;
      const nextAccessToken = await refreshAccessToken();

      if (nextAccessToken) {
        originalRequest.headers = originalRequest.headers || {};
        originalRequest.headers.Authorization = `Bearer ${nextAccessToken}`;
        return api(originalRequest);
      }

      clearAuthAndRedirect();
    }

    if (status === 401 && (!originalRequest || requestUrl.endsWith('/auth/login') || requestUrl.endsWith('/auth/refresh'))) {
      clearAuthAndRedirect();
    }

    return Promise.reject(error);
  }
);

export default api;
