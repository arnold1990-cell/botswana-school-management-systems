import axios from 'axios';
import { ACCESS_TOKEN_KEY } from './http';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(ACCESS_TOKEN_KEY);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  console.info('[api] request', config.method?.toUpperCase(), config.url, { hasToken: !!token });
  return config;
});

api.interceptors.response.use(
  (response) => {
    console.info('[api] response', response.status, response.config.url, response.data);
    return response;
  },
  async (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem(ACCESS_TOKEN_KEY);
      window.location.href = '/login';
    }

    throw error;
  }
);

export default api;
