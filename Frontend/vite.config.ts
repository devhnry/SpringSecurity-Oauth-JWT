import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

//https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: "../Backend/src/main/resources/static/",
    emptyOutDir: true, // also necessary
  },
  resolve: {
    alias: {
      "@": "./src"
    },
  },
});
