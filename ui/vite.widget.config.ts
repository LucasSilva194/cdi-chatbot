import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  build: {
    outDir: 'dist-widget',
    emptyOutDir: true,
    cssCodeSplit: false,
    lib: {
      entry: 'src/widget.ts',
      name: 'CDIChatbot',
      formats: ['iife'],
      fileName: () => 'cdi-chatbot-widget.js',
    },
  },
});
