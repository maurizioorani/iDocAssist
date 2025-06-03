import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  optimizeDeps: {
    exclude: [
      'mysql',
      'slack-node',
      'hipchat-notifier',
      'loggly',
      'mailgun-js',
      'nodemailer',
      'dtrace-provider'
    ]
  },
  build: {
    rollupOptions: {
      external: [
        'mysql',
        'slack-node',
        'hipchat-notifier',
        'loggly',
        'mailgun-js',
        'nodemailer',
        './build/**/*/DTraceProviderBindings'
      ]
    }
  }
})
