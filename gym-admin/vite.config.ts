import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig(({ mode }) => {
  const baseUrl = loadEnv(mode, process.cwd()).VITE_APP_BASE_API
  const targetUrl = loadEnv(mode, process.cwd()).VITE_BASE_URL

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': resolve(__dirname, 'src')
      }
    },
    server: {
      open: true,
      port: 5000,
      host: true,
      allowedHosts:["garth-gluier-acceptably.ngrok-free.dev"],
      proxy: {
        [baseUrl]: {
          target: targetUrl,
          changeOrigin: true,
          rewrite: (path) => path.replace(new RegExp(`^${baseUrl}`), '')
        }
      }
    }
  }
})
