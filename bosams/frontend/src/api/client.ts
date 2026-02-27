import axios from 'axios';
const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/api' });
api.interceptors.request.use((c)=>{const t=localStorage.getItem('accessToken'); if(t)c.headers.Authorization=`Bearer ${t}`; return c;});
api.interceptors.response.use(r=>r, async e=>{ if(e.response?.status===401){ localStorage.removeItem('accessToken'); window.location.href='/login'; } throw e;});
export default api;
