import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MatIconModule],
  template: `
    <header class="navbar">
      <div class="navbar-left">
        <button class="menu-btn" (click)="menuClick.emit()">
          <mat-icon>menu</mat-icon>
        </button>
        <div class="breadcrumb">
          <span class="breadcrumb-item">{{ title }}</span>
        </div>
      </div>

      <div class="navbar-right">
        <div class="user-info">
          <div class="user-avatar">
            <mat-icon>person</mat-icon>
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
}
