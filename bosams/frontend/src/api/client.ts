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
  (error) => {
    const status = error.response?.status;
    const requestUrl = `${error.config?.baseURL ?? ''}${error.config?.url ?? ''}`;
    const token = localStorage.getItem(ACCESS_TOKEN_KEY);
    console.info('[api] response', { url: requestUrl, hasToken: Boolean(token), status: status ?? 0 });

    if (status === 401 || status === 403) {
      clearAuthAndRedirect();
    }

    return Promise.reject(error);
  }
);

export default api;
