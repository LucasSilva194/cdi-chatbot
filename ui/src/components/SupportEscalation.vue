<template>
  <aside class="support-escalation">
    <div class="support-escalation__icon" aria-hidden="true">
      <LifeBuoy :size="20" />
    </div>
    <div class="support-escalation__content">
      <strong>Encaminhamento para suporte</strong>
      <p>Questões com conta, compra, pagamento ou acesso individual devem ser tratadas por suporte humano.</p>
    </div>
    <a class="support-escalation__action" href="mailto:info@cienciasdoinvestimento.com">
      <Mail :size="16" />
      Email
    </a>
  </aside>
</template>

<script setup lang="ts">
import { LifeBuoy, Mail } from '@lucide/vue';
</script>
