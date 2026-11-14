import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from './sidebar/sidebar.component';
import { NavbarComponent } from './navbar/navbar.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [NgIf, RouterOutlet, SidebarComponent, NavbarComponent],
  template: `
    <a class="skip-link" href="#conteudo-principal">Pular para o conteúdo</a>

    <div class="layout">
      <app-sidebar #sidebar />

      <div
        class="sidebar-overlay"
        *ngIf="sidebar.mobileOpen"
        (click)="sidebar.closeMobile()"
        aria-hidden="true"
      ></div>

      <div class="layout-main" [class.sidebar-collapsed]="sidebar.collapsed">
        <app-navbar title="MineBanco" (menuClick)="sidebar.toggle()" />
        <main id="conteudo-principal" class="layout-content" tabindex="-1">
          <router-outlet />
        </main>
      </div>
    </div>
  `,
  styleUrls: ['./layout.component.css']
})
export class LayoutComponent {}
