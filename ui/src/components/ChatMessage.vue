<template>
  <article class="chat-message" :class="[`chat-message--${message.role}`, { 'chat-message--escalated': message.escalated }]">
    <div class="chat-message__avatar" aria-hidden="true">
      <UserRound v-if="message.role === 'user'" :size="18" />
      <ShieldAlert v-else-if="message.intent === 'FINANCIAL_ADVICE_BLOCKED'" :size="18" />
      <img v-else :src="avatarUrl" alt="" />
    </div>

    <div class="chat-message__body">
      <div class="chat-message__meta">
        <span>{{ label }}</span>
        <span v-if="showDiagnostics && message.intent" class="chat-message__intent">{{ message.intent }}</span>
      </div>
      <p>{{ message.content }}</p>
      <p v-if="showDiagnostics && message.escalationReason" class="chat-message__reason">
        {{ message.escalationReason }}
      </p>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ShieldAlert, UserRound } from '@lucide/vue';
import type { ChatMessageItem } from '../types/chat';

const props = defineProps<{
  message: ChatMessageItem;
  avatarUrl?: string;
  showDiagnostics?: boolean;
}>();

const label = computed(() => (props.message.role === 'user' ? 'Cliente' : 'Genius'));
</script>
