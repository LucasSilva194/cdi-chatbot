import type { ChatRequest, ChatResponse } from '../types/chat';

let apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export function configureChatApi(options: { apiBaseUrl?: string }) {
  if (options.apiBaseUrl?.trim()) {
    apiBaseUrl = options.apiBaseUrl.trim().replace(/\/$/, '');
  }
}

export function getChatApiBaseUrl() {
  return apiBaseUrl;
}

export async function sendChatMessage(request: ChatRequest): Promise<ChatResponse> {
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
