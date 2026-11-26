import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';


export default defineConfig(({ command }) => ({
  plugins: [react()],
  // Nginx publica RoastLab desde la raíz del dominio (/).
  base: '/',
}));
