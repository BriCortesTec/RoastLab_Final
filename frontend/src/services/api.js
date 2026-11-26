import axios from 'axios';

// Define VITE_API_URL en frontend/.env cuando el backend tenga sus rutas listas.
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('roastlab_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use((response) => response, (error) => {
  if (axios.isCancel(error)) return Promise.reject(error);
  const message = error.response?.data?.message || error.message || 'No se pudo conectar con el servidor.';
  return Promise.reject(new Error(message));
});

export const authService = {
  login: (credentials) => api.post('/auth/login', credentials).then((response) => response.data),
  register: (details) => api.post('/auth/registro', details).then((response) => response.data),
};

export const resourceService = {
  list: (resource, { page = 0, size = 8, search = '', status = '', signal } = {}) => api.get(`/${resource}`, { params: { page, size, search, status }, signal }).then((response) => response.data),
  create: (resource, item) => api.post(`/${resource}`, item).then((response) => response.data),
  update: (resource, id, item) => api.put(`/${resource}/${id}`, item).then((response) => response.data),
  remove: (resource, id) => api.delete(`/${resource}/${id}`),
};

export const reportService = {
  summary: () => api.get('/reportes/resumen').then((response) => response.data),
};

