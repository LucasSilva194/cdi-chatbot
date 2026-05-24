export type Intent =
  | 'TRAINING_INFO'
  | 'PAYMENT_GENERAL'
  | 'ACCESS_GENERAL'
  | 'SUBSCRIPTION_GENERAL'
  | 'CERTIFICATION'
  | 'PERSONAL_ACCOUNT_ISSUE'
  | 'FINANCIAL_ADVICE_BLOCKED'
  | 'HUMAN_SUPPORT'
  | 'UNKNOWN';

export interface ChatRequest {
  conversationId?: string;
  message: string;
}

export interface ChatResponse {
  conversationId: string;
  intent: Intent;
  message: string;
  escalated: boolean;
  escalationReason: string | null;
  suggestions: string[];
}

export interface ChatMessageItem {
  id: string;
  role: 'assistant' | 'user';
  content: string;
  intent?: Intent;
  escalated?: boolean;
  escalationReason?: string | null;
}
