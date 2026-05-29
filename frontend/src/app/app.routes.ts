import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { DashboardComponent } from './layout/dashboard/dashboard.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'criar-conta', loadComponent: () => import('./criar-conta/criar-conta.component').then(m => m.CriarContaComponent) },
      { path: 'listar-contas', loadComponent: () => import('./listar-contas/listar-contas.component').then(m => m.ListarContasComponent) },
      { path: 'operacoes', loadComponent: () => import('./operacoes/operacoes.component').then(m => m.OperacoesComponent) },
      { path: 'relatorios', loadComponent: () => import('./relatorios/relatorios.component').then(m => m.RelatoriosComponent) },
    ]
  }
];
