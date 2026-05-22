<template>
  <section class="chat-widget" aria-label="Chat de suporte">
    <header class="chat-widget__header">
      <div>
        <p class="chat-widget__eyebrow">Ciências do Investimento</p>
        <h1>{{ title }}</h1>
      </div>
      <div class="chat-widget__status">
        <span aria-hidden="true"></span>
        FAQ ativo
      </div>
    </header>

    <div ref="messagesRef" class="chat-widget__messages">
      <ChatMessage v-for="message in messages" :key="message.id" :message="message" />
      <div v-if="sending" class="chat-widget__typing">
        <LoaderCircle :size="16" />
        A preparar resposta...
      </div>
    </div>

    <div v-if="lastEscalated" class="chat-widget__escalation">
      <SupportEscalation />
    </div>

    <div v-if="suggestions.length" class="chat-widget__suggestions" aria-label="Sugestões">
      <button v-for="suggestion in suggestions" :key="suggestion" type="button" @click="setDraft(suggestion)">
        {{ suggestion }}
      </button>
    </div>

    <ChatInput ref="inputRef" v-model="draft" :disabled="sending" @send="sendDraft" />
  </section>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from 'vue';
import { LoaderCircle } from '@lucide/vue';
import ChatInput from './ChatInput.vue';
import ChatMessage from './ChatMessage.vue';
import SupportEscalation from './SupportEscalation.vue';
import { sendChatMessage } from '../services/chatApi';
import type { ChatMessageItem } from '../types/chat';

withDefaults(defineProps<{
  title?: string;
}>(), {
  title: 'Assistente de suporte',
});

const conversationId = ref(createConversationId());
const draft = ref('');
const sending = ref(false);
const messagesRef = ref<HTMLElement | null>(null);
const inputRef = ref<InstanceType<typeof ChatInput> | null>(null);

const messages = ref<ChatMessageItem[]>([
  {
    id: createId(),
    role: 'assistant',
    content: 'Olá. Posso ajudar com dúvidas gerais sobre formações, pagamentos, subscrições, acesso ao site e recuperação de password.',
    intent: 'TRAINING_INFO',
  },
]);

const suggestions = ref<string[]>([
  'Que formações têm disponíveis?',
  'Como recupero a password?',
  'A formação dá certificado?',
]);

const lastEscalated = computed(() => messages.value.some((message) => message.role === 'assistant' && message.escalated));

async function sendDraft() {
  const content = draft.value.trim();
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

  try {
    const response = await sendChatMessage({
      conversationId: conversationId.value,
      message: content,
    });

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
    messages.value.push({
      id: createId(),
      role: 'assistant',
      content: 'Não foi possível contactar a API. Confirme se a API está ativa em http://localhost:8080.',
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

function setDraft(value: string) {
  draft.value = value;
  inputRef.value?.focus();
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

defineExpose({ setDraft, sendDraft });
</script>
