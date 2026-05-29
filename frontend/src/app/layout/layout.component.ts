import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from './sidebar/sidebar.component';
import { NavbarComponent } from './navbar/navbar.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, NavbarComponent],
  template: `
    <div class="layout">
      <app-sidebar #sidebar />
      <div class="layout-main" [class.sidebar-collapsed]="sidebar.collapsed">
        <app-navbar title="MineBanco" (menuClick)="sidebar.toggle()" />
        <main class="layout-content">
          <router-outlet />
        </main>
      </div>
    </div>
  `,
  styleUrls: ['./layout.component.css']
})
export class LayoutComponent {}
