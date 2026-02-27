import axios from 'axios';

export const ACCESS_TOKEN_KEY = 'bosams_access_token';
export const REFRESH_TOKEN_KEY = 'bosams_refresh_token';

const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem(ACCESS_TOKEN_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config as any;

    if (error.response?.status !== 401 || originalRequest?._retry) {
      throw error;
    }

    if (originalRequest?.url?.includes('/auth/refresh')) {
      localStorage.removeItem(ACCESS_TOKEN_KEY);
      localStorage.removeItem(REFRESH_TOKEN_KEY);
      window.location.href = '/login';
      throw error;
    }

    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    if (!refreshToken) {
      window.location.href = '/login';
      throw error;
    }

    originalRequest._retry = true;

    try {
      const refreshResponse = await axios.post(`${import.meta.env.VITE_API_URL || '/api'}/auth/refresh`, {
        refreshToken,
      });

      const newAccessToken = refreshResponse.data.accessToken;
      localStorage.setItem(ACCESS_TOKEN_KEY, newAccessToken);
      originalRequest.headers = originalRequest.headers || {};
      originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

      return http(originalRequest);
    } catch (refreshError) {
      localStorage.removeItem(ACCESS_TOKEN_KEY);
      localStorage.removeItem(REFRESH_TOKEN_KEY);
      window.location.href = '/login';
      throw refreshError;
    }
  }
);

export default http;
