import { Component, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { NgClass, NgFor, NgIf, CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ContaService } from '../../services/conta.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MatIconModule, NgClass, NgFor, NgIf, RouterLink, CurrencyPipe],
  template: `
    <div class="dashboard">
      <div class="page-header">
        <h1>Dashboard</h1>
        <p class="page-subtitle">Visão geral do sistema bancário</p>
      </div>

      <div class="stats-grid">
        <div class="stat-card" *ngFor="let stat of stats">
          <div class="stat-icon" [ngClass]="stat.color">
            <mat-icon>{{ stat.icon }}</mat-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
          <div class="stat-trend" [class.up]="stat.trend > 0" [class.down]="stat.trend < 0">
            <mat-icon>{{ stat.trend > 0 ? 'trending_up' : 'trending_down' }}</mat-icon>
            <span>{{ stat.trend > 0 ? '+' : '' }}{{ stat.trend }}%</span>
          </div>
        </div>
      </div>

      <div class="dashboard-grid">
        <div class="card recent-accounts">
          <div class="card-header">
            <h2>Contas Recentes</h2>
            <button class="card-action" routerLink="/listar-contas">
              <span>Ver todas</span>
              <mat-icon>arrow_forward</mat-icon>
            </button>
          </div>
          <div class="card-body">
            <div class="account-item" *ngFor="let conta of contasRecentes">
              <div class="account-avatar" [ngClass]="conta.tipo === 'CORRENTE' ? 'corrente' : 'poupanca'">
                <mat-icon>{{ conta.tipo === 'CORRENTE' ? 'account_balance_wallet' : 'savings' }}</mat-icon>
              </div>
              <div class="account-info">
                <span class="account-name">{{ conta.clienteNome }}</span>
                <span class="account-number">Conta {{ conta.tipo }} • {{ conta.numero }}</span>
              </div>
              <span class="account-balance" [class.negative]="conta.saldo < 0">
                {{ conta.saldo | currency:'BRL' }}
              </span>
            </div>
            <div class="empty-state" *ngIf="contasRecentes.length === 0">
              <mat-icon>account_balance_wallet</mat-icon>
              <p>Nenhuma conta cadastrada</p>
            </div>
          </div>
        </div>

        <div class="card quick-actions">
          <div class="card-header">
            <h2>Ações Rápidas</h2>
          </div>
          <div class="card-body">
            <button class="action-btn" routerLink="/criar-conta">
              <span class="action-icon new">
                <mat-icon>person_add</mat-icon>
              </span>
              <span class="action-text">Nova Conta</span>
            </button>
            <button class="action-btn" routerLink="/operacoes">
              <span class="action-icon deposit">
                <mat-icon>payments</mat-icon>
              </span>
              <span class="action-text">Depósito</span>
            </button>
            <button class="action-btn" routerLink="/operacoes">
              <span class="action-icon transfer">
                <mat-icon>swap_horiz</mat-icon>
              </span>
              <span class="action-text">Transferência</span>
            </button>
            <button class="action-btn" routerLink="/relatorios">
              <span class="action-icon report">
                <mat-icon>assessment</mat-icon>
              </span>
              <span class="action-text">Relatórios</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats = [
    { icon: 'account_balance', label: 'Total de Contas', value: '0', color: 'blue', trend: 0 },
    { icon: 'account_balance_wallet', label: 'Saldo Total', value: 'R$ 0,00', color: 'green', trend: 0 },
    { icon: 'payments', label: 'Depósitos', value: 'R$ 0,00', color: 'purple', trend: 0 },
    { icon: 'receipt', label: 'Impostos', value: 'R$ 0,00', color: 'orange', trend: 0 },
  ];

  contasRecentes: any[] = [];

  constructor(private contaService: ContaService) {}

  ngOnInit() {
    this.contaService.listar().subscribe({
      next: (contas) => {
        this.contasRecentes = contas.slice(0, 5);
        this.updateStats(contas);
      },
      error: () => {}
    });
  }

  private updateStats(contas: any[]) {
    const total = contas.length;
    const saldoTotal = contas.reduce((s, c) => s + Number(c.saldo), 0);

    this.stats = [
      { icon: 'account_balance', label: 'Total de Contas', value: String(total), color: 'blue', trend: 0 },
      { icon: 'account_balance_wallet', label: 'Saldo Total', value: this.formatCurrency(saldoTotal), color: 'green', trend: 0 },
      { icon: 'payments', label: 'Depósitos', value: 'R$ 0,00', color: 'purple', trend: 0 },
      { icon: 'receipt', label: 'Impostos', value: 'R$ 0,00', color: 'orange', trend: 0 },
    ];
  }

  private formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }
}
