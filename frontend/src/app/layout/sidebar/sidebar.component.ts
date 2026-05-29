import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { NgClass, NgIf } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, MatIconModule, NgClass, NgIf],
  template: `
    <aside class="sidebar" [class.collapsed]="collapsed">
      <div class="sidebar-header">
        <mat-icon class="sidebar-logo">account_balance</mat-icon>
        <span class="sidebar-brand" *ngIf="!collapsed">MineBanco</span>
        <button class="toggle-btn" (click)="toggle()">
          <mat-icon>{{ collapsed ? 'chevron_right' : 'chevron_left' }}</mat-icon>
        </button>
      </div>

      <nav class="sidebar-nav">
        <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
          <mat-icon>dashboard</mat-icon>
          <span class="nav-label">Dashboard</span>
        </a>
        <a routerLink="/criar-conta" routerLinkActive="active" class="nav-item">
          <mat-icon>person_add</mat-icon>
          <span class="nav-label">Criar Conta</span>
        </a>
        <a routerLink="/listar-contas" routerLinkActive="active" class="nav-item">
          <mat-icon>format_list_bulleted</mat-icon>
          <span class="nav-label">Listar Contas</span>
        </a>
        <a routerLink="/operacoes" routerLinkActive="active" class="nav-item">
          <mat-icon>swap_horiz</mat-icon>
          <span class="nav-label">Operações</span>
        </a>
        <a routerLink="/relatorios" routerLinkActive="active" class="nav-item">
          <mat-icon>assessment</mat-icon>
          <span class="nav-label">Relatórios</span>
        </a>
      </nav>

      <div class="sidebar-footer">
        <a routerLink="/" class="nav-item">
          <mat-icon>help_outline</mat-icon>
          <span class="nav-label">Ajuda</span>
        </a>
      </div>
    </aside>
  `,
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  collapsed = false;

  toggle() {
    this.collapsed = !this.collapsed;
  }
}
