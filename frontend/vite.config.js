import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(({ mode, command }) => {
  const env = loadEnv(mode, process.cwd(), 'VITE_');
  const url = env.VITE_API_URL || (command === 'serve' ? 'http://localhost:8080/api' : '/api');
  return {
    plugins: [react()],
    base: '/',
    define: { 'import.meta.env.VITE_API_URL': JSON.stringify(url) },
  };
});
