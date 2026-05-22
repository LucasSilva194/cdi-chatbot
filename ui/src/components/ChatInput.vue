<template>
  <form class="chat-input" @submit.prevent="emitSend">
    <textarea
      ref="textareaRef"
      :value="modelValue"
      :disabled="disabled"
      class="chat-input__field"
      rows="3"
      maxlength="1200"
      placeholder="Escreva a mensagem..."
      @input="updateValue"
      @keydown.enter.exact.prevent="emitSend"
    />
    <button class="chat-input__send" type="submit" :disabled="disabled || !modelValue.trim()" aria-label="Enviar mensagem" title="Enviar mensagem">
      <SendHorizontal :size="20" />
    </button>
  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { SendHorizontal } from '@lucide/vue';

defineProps<{
  modelValue: string;
  disabled?: boolean;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: string];
  send: [];
}>();

const textareaRef = ref<HTMLTextAreaElement | null>(null);

function updateValue(event: Event) {
  emit('update:modelValue', (event.target as HTMLTextAreaElement).value);
}

function emitSend() {
  emit('send');
}

function focus() {
  textareaRef.value?.focus();
}

defineExpose({ focus });
</script>
