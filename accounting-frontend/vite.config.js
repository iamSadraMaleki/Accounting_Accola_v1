import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173, // همون پورتی که الان داری
    allowedHosts: [
      '.loca.lt', // برای localtunnel
      '.ngrok-free.app', // برای ngrok
    ],
  },
})
