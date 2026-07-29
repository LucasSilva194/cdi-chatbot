<template>
  <main class="demo-page">
    <section class="demo-page__intro" aria-labelledby="demo-title">
      <div class="demo-page__brand">
        <img src="/cdi-chatbot-avatar.png" alt="" />
        <div>
          <p>Ciências do Investimento</p>
          <h1 id="demo-title">Genius</h1>
        </div>
      </div>

      <p class="demo-page__lead">
        Widget de suporte ao cliente para responder a dúvidas frequentes sobre formações,
        pagamentos, certificados, IRS, acesso ao site e problemas técnicos.
      </p>

      <div class="demo-page__badges" aria-label="Características principais">
        <span><Bot :size="16" /> Demo interativa</span>
        <span><ShieldCheck :size="16" /> Sem aconselhamento financeiro</span>
        <span><LockKeyhole :size="16" /> Read-only</span>
      </div>
    </section>

    <section class="demo-page__grid" aria-label="Capacidades do chatbot">
      <article class="demo-feature">
        <GraduationCap :size="22" />
        <h2>Formações e certificados</h2>
        <p>Explica a oferta disponível, orienta quem está a começar e informa sobre certificação DGERT.</p>
      </article>

      <article class="demo-feature">
        <ReceiptText :size="22" />
        <h2>Pagamentos, faturas e IRS</h2>
        <p>Responde a dúvidas gerais sobre métodos de pagamento, faturação, isenção de IVA e dedução em IRS.</p>
      </article>

      <article class="demo-feature">
        <LifeBuoy :size="22" />
        <h2>Acesso e suporte técnico</h2>
        <p>Ajuda com código de login por email, subscrições e troubleshooting de vídeos privados.</p>
      </article>
    </section>

    <section class="demo-page__prompts" aria-labelledby="prompt-title">
      <div>
        <p class="demo-page__eyebrow">Experimente</p>
        <h2 id="prompt-title">Perguntas que pode fazer</h2>
      </div>

      <div class="demo-page__prompt-list">
        <button
          v-for="prompt in demoPrompts"
          :key="prompt"
          type="button"
          @click="sendPrompt(prompt)"
        >
          {{ prompt }}
        </button>
      </div>
    </section>

    <section class="demo-page__note" aria-label="Limites da demo">
      <ShieldCheck :size="20" />
      <p>
        Esta demo corre sem API externa. Em produção, o mesmo widget pode chamar a API Spring Boot
        para registar logs, consultar dados permitidos e manter todos os fluxos em modo read-only.
      </p>
    </section>
  </main>

  <ChatWidget
    ref="chatWidgetRef"
    title="Genius"
    :chat-mode="chatMode"
    :initially-open="chatMode === 'demo'"
  />
</template>

<script setup lang="ts">
import { nextTick, ref } from 'vue';
import { Bot, GraduationCap, LifeBuoy, LockKeyhole, ReceiptText, ShieldCheck } from '@lucide/vue';
import ChatWidget from './components/ChatWidget.vue';

const hasApiBaseUrl = Boolean(import.meta.env.VITE_API_BASE_URL?.trim());
const chatMode = import.meta.env.VITE_CHAT_MODE === 'demo'
  || (import.meta.env.VITE_CHAT_MODE !== 'api' && import.meta.env.PROD && !hasApiBaseUrl)
  ? 'demo'
  : 'api';

const chatWidgetRef = ref<InstanceType<typeof ChatWidget> | null>(null);

const demoPrompts = [
  'Que formações têm disponíveis?',
  'Qual o melhor curso para começar?',
  'A formação dá certificado?',
  'A formação entra no IRS?',
  'Não consigo ver os vídeos do Trader I',
  'Devo investir neste ETF?',
];

async function sendPrompt(prompt: string) {
  chatWidgetRef.value?.setDraft(prompt);
  await nextTick();
  await chatWidgetRef.value?.sendDraft();
}
</script>
