import type { ChatRequest, ChatResponse } from '../types/chat';

const configuredApiBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim();

let apiBaseUrl = configuredApiBaseUrl || 'http://localhost:8080';
let chatMode = resolveInitialChatMode();

export function configureChatApi(options: { apiBaseUrl?: string; chatMode?: string }) {
  if (options.apiBaseUrl?.trim()) {
    apiBaseUrl = options.apiBaseUrl.trim().replace(/\/$/, '');
  }

  if (options.chatMode === 'demo' || options.chatMode === 'api') {
    chatMode = options.chatMode;
  }
}

export function getChatApiBaseUrl() {
  return apiBaseUrl;
}

export function getChatMode() {
  return chatMode;
}

export async function sendChatMessage(request: ChatRequest): Promise<ChatResponse> {
  if (chatMode === 'demo') {
    const { sendDemoChatMessage } = await import('./demoChatApi');
    return sendDemoChatMessage(request);
  }

  const response = await fetch(`${apiBaseUrl}/api/chat`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    throw new Error(`API returned ${response.status}`);
  }

  return response.json() as Promise<ChatResponse>;
}

function resolveInitialChatMode() {
  if (import.meta.env.VITE_CHAT_MODE === 'demo') {
    return 'demo';
  }

  if (import.meta.env.VITE_CHAT_MODE === 'api') {
    return 'api';
  }

  return import.meta.env.PROD && !configuredApiBaseUrl ? 'demo' : 'api';
}
