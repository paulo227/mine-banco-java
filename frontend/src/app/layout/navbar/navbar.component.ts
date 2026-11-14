import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { ThemeService } from '../../services/theme.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MatIconModule],
  template: `
    <header class="navbar">
      <div class="navbar-left">
        <button
          class="menu-btn"
          type="button"
          (click)="menuClick.emit()"
          aria-label="Alternar menu lateral"
          aria-controls="app-sidebar"
        >
          <mat-icon aria-hidden="true">menu</mat-icon>
        </button>

        <div class="breadcrumb">
          <span class="breadcrumb-item">{{ title }}</span>
        </div>
      </div>

      <div class="navbar-right">
        <button
          class="theme-toggle"
          type="button"
          (click)="themeService.toggle()"
          [attr.aria-label]="themeService.isDark() ? 'Ativar tema claro' : 'Ativar tema escuro'"
          [attr.aria-pressed]="themeService.isDark()"
          [attr.title]="themeService.isDark() ? 'Mudar para tema claro' : 'Mudar para tema escuro'"
        >
          <mat-icon aria-hidden="true">{{ themeService.isDark() ? 'light_mode' : 'dark_mode' }}</mat-icon>
        </button>

        <div class="user-info">
          <div class="user-avatar" aria-hidden="true">
            <mat-icon aria-hidden="true">person</mat-icon>
          </div>
          <div class="user-details">
            <span class="user-name">Admin</span>
            <span class="user-role">Sistema Bancário</span>
          </div>
        </div>
      </div>
    </header>
  `,
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  @Input() title = 'Dashboard';
  @Output() menuClick = new EventEmitter<void>();

  constructor(public readonly themeService: ThemeService) {}
}
