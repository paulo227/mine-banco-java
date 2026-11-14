import { Component, HostListener } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { NgClass, NgIf } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, MatIconModule, NgClass, NgIf],
  template: `
    <aside
      id="app-sidebar"
      class="sidebar"
      [class.collapsed]="collapsed"
      [class.mobile-open]="mobileOpen"
      aria-label="Menu principal"
    >
      <div class="sidebar-header">
        <span class="logo-tile" aria-hidden="true">
          <mat-icon class="sidebar-logo">account_balance</mat-icon>
        </span>
        <span class="sidebar-brand" *ngIf="!collapsed">MineBanco</span>
        <button
          class="toggle-btn"
          type="button"
          (click)="toggle()"
          [attr.aria-label]="isMobile ? (mobileOpen ? 'Fechar menu' : 'Abrir menu') : (collapsed ? 'Expandir menu' : 'Recolher menu')"
          [attr.aria-expanded]="isMobile ? mobileOpen : !collapsed"
          aria-controls="app-sidebar"
        >
          <mat-icon aria-hidden="true">{{ collapsed ? 'chevron_right' : 'chevron_left' }}</mat-icon>
        </button>
      </div>

      <nav class="sidebar-nav" aria-label="Navegação principal">
        <a
          routerLink="/dashboard"
          routerLinkActive="active"
          ariaCurrentWhenActive="page"
          class="nav-item"
          (click)="onNavigate()"
          [attr.title]="collapsed ? 'Dashboard' : null"
        >
          <mat-icon aria-hidden="true">dashboard</mat-icon>
          <span class="nav-label">Dashboard</span>
        </a>
        <a
          routerLink="/criar-conta"
          routerLinkActive="active"
          ariaCurrentWhenActive="page"
          class="nav-item"
          (click)="onNavigate()"
          [attr.title]="collapsed ? 'Criar Conta' : null"
        >
          <mat-icon aria-hidden="true">person_add</mat-icon>
          <span class="nav-label">Criar Conta</span>
        </a>
        <a
          routerLink="/listar-contas"
          routerLinkActive="active"
          ariaCurrentWhenActive="page"
          class="nav-item"
          (click)="onNavigate()"
          [attr.title]="collapsed ? 'Listar Contas' : null"
        >
          <mat-icon aria-hidden="true">format_list_bulleted</mat-icon>
          <span class="nav-label">Listar Contas</span>
        </a>
        <a
          routerLink="/operacoes"
          routerLinkActive="active"
          ariaCurrentWhenActive="page"
          class="nav-item"
          (click)="onNavigate()"
          [attr.title]="collapsed ? 'Operações' : null"
        >
          <mat-icon aria-hidden="true">swap_horiz</mat-icon>
          <span class="nav-label">Operações</span>
        </a>
        <a
          routerLink="/relatorios"
          routerLinkActive="active"
          ariaCurrentWhenActive="page"
          class="nav-item"
          (click)="onNavigate()"
          [attr.title]="collapsed ? 'Relatórios' : null"
        >
          <mat-icon aria-hidden="true">assessment</mat-icon>
          <span class="nav-label">Relatórios</span>
        </a>
      </nav>

      <div class="sidebar-footer">
        <a
          routerLink="/dashboard"
          routerLinkActive="active"
          ariaCurrentWhenActive="page"
          class="nav-item"
          (click)="onNavigate()"
          [attr.title]="collapsed ? 'Ajuda' : null"
        >
          <mat-icon aria-hidden="true">help_outline</mat-icon>
          <span class="nav-label">Ajuda</span>
        </a>
      </div>
    </aside>
  `,
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  collapsed = false;
  mobileOpen = false;
  isMobile = false;

  private mobileQuery?: MediaQueryList;

  constructor() {
    if (typeof window !== 'undefined' && window.matchMedia) {
      this.mobileQuery = window.matchMedia('(max-width: 768px)');
      this.isMobile = this.mobileQuery.matches;

      const listener = (event: MediaQueryListEvent) => {
        this.isMobile = event.matches;
        if (event.matches) {
          this.collapsed = false;
        } else {
          this.mobileOpen = false;
        }
      };

      this.mobileQuery.addEventListener?.('change', listener);
    }
  }

  toggle(): void {
    if (this.isMobile) {
      this.mobileOpen = !this.mobileOpen;
      return;
    }
    this.collapsed = !this.collapsed;
  }

  closeMobile(): void {
    this.mobileOpen = false;
  }

  onNavigate(): void {
    if (this.isMobile) {
      this.mobileOpen = false;
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.mobileOpen = false;
  }
}
