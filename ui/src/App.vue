<template>
  <ChatWidget title="Genius" :chat-mode="chatMode" :initially-open="chatMode === 'demo'" />
</template>

<script setup lang="ts">
import ChatWidget from './components/ChatWidget.vue';

const chatMode = import.meta.env.VITE_CHAT_MODE === 'demo' ? 'demo' : 'api';
</script>
