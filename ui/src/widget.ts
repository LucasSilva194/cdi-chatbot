import { createApp, type App as VueApp } from 'vue';
import ChatWidget from './components/ChatWidget.vue';
import './styles/main.css';

export type CDIChatbotWidgetOptions = {
  target?: string | HTMLElement;
  apiBaseUrl?: string;
  avatarUrl?: string;
  title?: string;
  initiallyOpen?: boolean;
  floating?: boolean;
  showDiagnostics?: boolean;
};

export type CDIChatbotWidgetInstance = {
  app: VueApp<Element>;
  element: HTMLElement;
  unmount: () => void;
};

function mount(options: CDIChatbotWidgetOptions = {}): CDIChatbotWidgetInstance {
  const element = resolveTarget(options.target);
  const app = createApp(ChatWidget, {
    title: options.title ?? 'Genius',
    apiBaseUrl: options.apiBaseUrl,
    avatarUrl: options.avatarUrl ?? '/cdi-chatbot-avatar.png',
    initiallyOpen: options.initiallyOpen ?? false,
    floating: options.floating ?? true,
    showDiagnostics: options.showDiagnostics ?? false,
  });

  app.mount(element);

  return {
    app,
    element,
    unmount: () => app.unmount(),
  };
}

function resolveTarget(target?: string | HTMLElement) {
  if (target instanceof HTMLElement) {
    return target;
  }

  if (typeof target === 'string') {
    const element = document.querySelector<HTMLElement>(target);
    if (element) {
      return element;
    }
  }

  const element = document.createElement('div');
  element.id = 'cdi-chatbot-root';
  document.body.appendChild(element);
  return element;
}

function readBoolean(value: string | undefined, fallback: boolean) {
  if (value === undefined) {
    return fallback;
  }

  return value === 'true';
}

function readScriptOptions(): CDIChatbotWidgetOptions & { autoMount: boolean } {
  const script = document.currentScript as HTMLScriptElement | null;
  const dataset = script?.dataset ?? {};

  return {
    autoMount: dataset.autoMount !== 'false',
    target: dataset.target,
    apiBaseUrl: dataset.apiBaseUrl,
    avatarUrl: dataset.avatarUrl,
    title: dataset.title,
    initiallyOpen: readBoolean(dataset.initiallyOpen, false),
    floating: readBoolean(dataset.floating, true),
    showDiagnostics: readBoolean(dataset.showDiagnostics, false),
  };
}

declare global {
  interface Window {
    CDIChatbot?: {
      mount: typeof mount;
    };
  }
}

window.CDIChatbot = { mount };

const scriptOptions = readScriptOptions();

if (scriptOptions.autoMount) {
  mount(scriptOptions);
}

export { mount };
