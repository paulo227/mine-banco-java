import { Injectable, computed, effect, signal } from '@angular/core';

export type Theme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly storageKey = 'minebanco-theme';

  readonly theme = signal<Theme>(this.resolveInitialTheme());
  readonly isDark = computed(() => this.theme() === 'dark');

  constructor() {
    effect(() => this.applyTheme(this.theme()));
    this.watchSystemPreference();
  }

  toggle(): void {
    this.setTheme(this.isDark() ? 'light' : 'dark');
  }

  setTheme(theme: Theme): void {
    this.theme.set(theme);
    try {
      localStorage.setItem(this.storageKey, theme);
    } catch {
      /* armazenamento indisponível */
    }
  }

  private resolveInitialTheme(): Theme {
    if (typeof window === 'undefined') {
      return 'light';
    }

    try {
      const stored = localStorage.getItem(this.storageKey);
      if (stored === 'light' || stored === 'dark') {
        return stored;
      }
    } catch {
      /* armazenamento indisponível */
    }

    return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }

  private watchSystemPreference(): void {
    if (typeof window === 'undefined' || !window.matchMedia) {
      return;
    }

    const query = window.matchMedia('(prefers-color-scheme: dark)');
    const listener = (event: MediaQueryListEvent) => {
      try {
        if (!localStorage.getItem(this.storageKey)) {
          this.theme.set(event.matches ? 'dark' : 'light');
        }
      } catch {
        this.theme.set(event.matches ? 'dark' : 'light');
      }
    };

    if (typeof query.addEventListener === 'function') {
      query.addEventListener('change', listener);
    }
  }

  private applyTheme(theme: Theme): void {
    if (typeof document === 'undefined') {
      return;
    }

    const root = document.documentElement;
    const dark = theme === 'dark';

    root.classList.toggle('dark-theme', dark);
    root.dataset['theme'] = theme;
    root.style.colorScheme = theme;

    document
      .querySelector('meta[name="theme-color"]')
      ?.setAttribute('content', dark ? '#0b1120' : '#f6f8fc');
  }
}
