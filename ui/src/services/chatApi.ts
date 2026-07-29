import type { ChatRequest, ChatResponse } from '../types/chat';

let apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
let chatMode = import.meta.env.VITE_CHAT_MODE === 'demo' ? 'demo' : 'api';

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
