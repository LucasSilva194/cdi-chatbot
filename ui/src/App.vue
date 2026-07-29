<template>
  <ChatWidget title="Genius" :chat-mode="chatMode" :initially-open="chatMode === 'demo'" />
</template>

<script setup lang="ts">
import ChatWidget from './components/ChatWidget.vue';

const hasApiBaseUrl = Boolean(import.meta.env.VITE_API_BASE_URL?.trim());
const chatMode = import.meta.env.VITE_CHAT_MODE === 'demo'
  || (import.meta.env.VITE_CHAT_MODE !== 'api' && import.meta.env.PROD && !hasApiBaseUrl)
  ? 'demo'
  : 'api';
</script>
