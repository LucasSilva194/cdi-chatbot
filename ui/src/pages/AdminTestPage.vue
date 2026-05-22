<template>
  <main class="admin-page">
    <section class="admin-page__workspace">
      <aside class="admin-page__sidebar" aria-label="Cenários de teste">
        <div class="admin-page__brand">
          <span class="admin-page__mark">CDI</span>
          <div>
            <p>Chatbot</p>
            <strong>Suporte ao cliente</strong>
          </div>
        </div>

        <div class="admin-page__section">
          <h2>Exemplos</h2>
          <button v-for="prompt in prompts" :key="prompt" type="button" @click="usePrompt(prompt)">
            <MessageSquareText :size="16" />
            {{ prompt }}
          </button>
        </div>

        <div class="admin-page__section">
          <h2>Guardrails</h2>
          <div class="admin-page__pill">
            <ShieldCheck :size="16" />
            Sem aconselhamento financeiro
          </div>
          <div class="admin-page__pill admin-page__pill--accent">
            <LifeBuoy :size="16" />
            Suporte humano para casos pessoais
          </div>
        </div>
      </aside>

      <ChatWidget ref="chatWidgetRef" title="Painel de teste" />
    </section>
  </main>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { LifeBuoy, MessageSquareText, ShieldCheck } from '@lucide/vue';
import ChatWidget from '../components/ChatWidget.vue';

const chatWidgetRef = ref<InstanceType<typeof ChatWidget> | null>(null);

const prompts = [
  'Que formações têm disponíveis?',
  'Não consigo aceder ao curso',
  'Paguei mas ainda não tenho acesso',
  'Como recupero a password?',
  'A formação dá certificado?',
  'Devo investir neste ETF?',
  'Qual o melhor curso para começar?',
];

function usePrompt(prompt: string) {
  chatWidgetRef.value?.setDraft(prompt);
}
</script>
