<template>
  <div class="chat-widget-shell" :class="{ 'chat-widget-shell--floating': floating, 'chat-widget-shell--inline': !floating }">
    <button
      v-if="floating && !isOpen"
      class="chat-widget-launcher"
      type="button"
      aria-label="Abrir chat de suporte"
      title="Abrir chat de suporte"
      @click="open"
    >
      <img :src="avatarUrl" alt="" />
      <span>Ajuda</span>
    </button>

    <section v-if="isOpen" class="chat-widget" aria-label="Chat de suporte">
      <header class="chat-widget__header">
        <div class="chat-widget__brand">
          <img :src="avatarUrl" alt="" />
          <div>
            <p class="chat-widget__eyebrow">Ciências do Investimento</p>
            <h1>{{ title }}</h1>
          </div>
        </div>
        <div class="chat-widget__header-actions">
          <div class="chat-widget__status">
            <span aria-hidden="true"></span>
            Genius
          </div>
          <button
            v-if="floating"
            class="chat-widget__icon-button"
            type="button"
            aria-label="Minimizar chat"
            title="Minimizar chat"
            @click="close"
          >
            <Minus :size="18" />
          </button>
        </div>
      </header>

      <div ref="messagesRef" class="chat-widget__messages">
        <ChatMessage
          v-for="message in messages"
          :key="message.id"
          :message="message"
          :avatar-url="avatarUrl"
          :show-diagnostics="showDiagnostics"
        />
        <div v-if="sending" class="chat-widget__typing">
          <LoaderCircle :size="16" />
          A pensar na melhor resposta...
        </div>
      </div>

      <div v-if="lastEscalated" class="chat-widget__escalation">
        <SupportEscalation />
      </div>

      <div v-if="suggestions.length" class="chat-widget__suggestions" aria-label="Sugestões">
        <button
          v-for="suggestion in suggestions"
          :key="suggestion"
          type="button"
          :disabled="sending"
          @click="sendSuggestion(suggestion)"
        >
          {{ suggestion }}
        </button>
      </div>

      <ChatInput ref="inputRef" v-model="draft" :disabled="sending" @send="sendDraft" />
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from 'vue';
import { LoaderCircle, Minus } from '@lucide/vue';
import ChatInput from './ChatInput.vue';
import ChatMessage from './ChatMessage.vue';
import SupportEscalation from './SupportEscalation.vue';
import { configureChatApi, getChatApiBaseUrl, sendChatMessage } from '../services/chatApi';
import type { ChatMessageItem } from '../types/chat';

const props = withDefaults(defineProps<{
  title?: string;
  apiBaseUrl?: string;
  avatarUrl?: string;
  initiallyOpen?: boolean;
  floating?: boolean;
  showDiagnostics?: boolean;
}>(), {
  title: 'Genius',
  apiBaseUrl: undefined,
  avatarUrl: '/cdi-chatbot-avatar.png',
  initiallyOpen: false,
  floating: true,
  showDiagnostics: false,
});

configureChatApi({ apiBaseUrl: props.apiBaseUrl });

const isOpen = ref(props.initiallyOpen || !props.floating);
const conversationId = ref(createConversationId());
const draft = ref('');
const sending = ref(false);
const messagesRef = ref<HTMLElement | null>(null);
const inputRef = ref<InstanceType<typeof ChatInput> | null>(null);
const MIN_THINKING_TIME_MS = 850;

const messages = ref<ChatMessageItem[]>([
  {
    id: createId(),
    role: 'assistant',
    content: 'Olá, sou o Genius, o assistente da Ciências do Investimento. Diga-me o que precisa e eu tento orientar. Posso ajudar com formações, pagamentos gerais, subscrições, acesso ao site e código de login por email.',
    intent: 'TRAINING_INFO',
  },
]);

const suggestions = ref<string[]>([
  'Que formações têm disponíveis?',
  'Não recebi o código',
  'A formação dá certificado?',
]);

const lastEscalated = computed(() => {
  const lastAssistantMessage = [...messages.value].reverse().find((message) => message.role === 'assistant');

  return lastAssistantMessage?.escalated === true;
});

async function sendDraft() {
  await sendMessage(draft.value);
}

async function sendSuggestion(value: string) {
  open();
  await sendMessage(value);
}

async function sendMessage(value: string) {
  const content = value.trim();
  if (!content || sending.value) {
    return;
  }

  draft.value = '';
  suggestions.value = [];
  messages.value.push({
    id: createId(),
    role: 'user',
    content,
  });

  sending.value = true;
  await scrollToBottom();
  const thinkingStartedAt = Date.now();

  try {
    const response = await sendChatMessage({
      conversationId: conversationId.value,
      message: content,
    });

    await waitForMinimumThinkingTime(thinkingStartedAt);
    conversationId.value = response.conversationId;
    messages.value.push({
      id: createId(),
      role: 'assistant',
      content: response.message,
      intent: response.intent,
      escalated: response.escalated,
      escalationReason: response.escalationReason,
    });
    suggestions.value = response.suggestions ?? [];
  } catch {
    await waitForMinimumThinkingTime(thinkingStartedAt);
    messages.value.push({
      id: createId(),
      role: 'assistant',
      content: `Não foi possível contactar a API. Confirme se a API está ativa em ${getChatApiBaseUrl()}.`,
      intent: 'UNKNOWN',
      escalated: true,
      escalationReason: 'Falha de comunicação com a API.',
    });
  } finally {
    sending.value = false;
    await scrollToBottom();
    inputRef.value?.focus();
  }
}

function waitForMinimumThinkingTime(startedAt: number) {
  const elapsed = Date.now() - startedAt;
  const remaining = MIN_THINKING_TIME_MS - elapsed;

  if (remaining <= 0) {
    return Promise.resolve();
  }

  return new Promise((resolve) => window.setTimeout(resolve, remaining));
}

function setDraft(value: string) {
  open();
  draft.value = value;
  nextTick(() => inputRef.value?.focus());
}

function open() {
  isOpen.value = true;
  nextTick(async () => {
    await scrollToBottom();
    inputRef.value?.focus();
  });
}

function close() {
  isOpen.value = false;
}

async function scrollToBottom() {
  await nextTick();
  const element = messagesRef.value;
  if (element) {
    element.scrollTop = element.scrollHeight;
  }
}

function createConversationId() {
  return `conversation-${createId()}`;
}

function createId() {
  if ('crypto' in window && 'randomUUID' in window.crypto) {
    return window.crypto.randomUUID();
  }

  return Math.random().toString(36).slice(2);
}

defineExpose({ setDraft, sendDraft, open, close });
</script>
