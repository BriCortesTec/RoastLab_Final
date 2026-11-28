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
  const failure = new Error(message);
  failure.fieldErrors = error.response?.data?.fieldErrors || {};
  failure.status = error.response?.status;
  return Promise.reject(failure);
});

export const authService = {
  login: (credentials) => api.post('/auth/login', credentials).then((response) => response.data),
  me: () => api.get('/auth/me').then(response => response.data),
  logout: () => api.post('/auth/logout'),
  updateProfile: (fields) => api.put('/auth/me', fields).then(response => response.data),
  forgotPassword: (email) => api.post('/auth/forgot-password', {email}).then(response => response.data),
  resendVerification: (email) => api.post('/auth/resend-verification', {email}).then(response => response.data),
  resetPassword: (token, password) => api.post('/auth/reset-password', {token, password}).then(response => response.data),
  verifyEmail: (token) => api.post('/auth/verify-email', {token}).then(response => response.data),
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

