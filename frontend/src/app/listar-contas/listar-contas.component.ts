import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { ContaService } from '../services/conta.service';
import { ContaResponse } from '../models/conta.model';

@Component({
  selector: 'app-listar-contas',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatSnackBarModule],
  template: `
    <div class="page">
      <header class="page-header">
        <div>
          <h1 class="page-title">Contas cadastradas</h1>
          <p class="page-subtitle" aria-live="polite">{{ contas.length }} conta(s) encontrada(s)</p>
        </div>
        <button mat-stroked-button color="primary" type="button" (click)="carregar()" [disabled]="loading">
          <mat-icon aria-hidden="true">refresh</mat-icon>
          Atualizar
        </button>
      </header>

      <div *ngIf="loading" class="loading" role="status" aria-live="polite">
        <mat-icon class="spin" aria-hidden="true">refresh</mat-icon>
        <span>Carregando contas...</span>
      </div>

      <div *ngIf="!loading && contas.length === 0" class="empty" role="status">
        <mat-icon aria-hidden="true">account_balance_wallet</mat-icon>
        <p>Nenhuma conta cadastrada.</p>
      </div>

      <div class="card-grid" *ngIf="!loading" aria-label="Lista de contas">
        <article
          *ngFor="let c of contas"
          class="conta-card surface-card"
          [class.poupanca]="c.tipo === 'POUPANCA'"
        >
          <div class="card-top">
            <span class="tipo-badge" [class.corrente]="c.tipo === 'CORRENTE'" [class.poupanca]="c.tipo === 'POUPANCA'">
              <mat-icon aria-hidden="true">{{ c.tipo === 'CORRENTE' ? 'account_balance' : 'savings' }}</mat-icon>
              {{ c.tipo }}
            </span>
            <span class="numero">#{{ c.numero }}</span>
          </div>

          <div class="card-body">
            <div class="info-row">
              <mat-icon aria-hidden="true">person</mat-icon>
              <span>{{ c.clienteNome }} <span class="cpf">(CPF: {{ c.clienteCpf }})</span></span>
            </div>
            <div class="info-row">
              <mat-icon aria-hidden="true">payments</mat-icon>
              <span><strong>Saldo:</strong> {{ c.saldo | currency:'BRL' }}</span>
            </div>
            <div class="info-row" *ngIf="c.tipo === 'CORRENTE'">
              <mat-icon aria-hidden="true">credit_card</mat-icon>
              <span><strong>Limite:</strong> {{ c.limite | currency:'BRL' }}</span>
            </div>
            <div class="info-row">
              <mat-icon aria-hidden="true">receipt</mat-icon>
              <span><strong>Imposto:</strong> {{ c.imposto | currency:'BRL' }}</span>
            </div>
          </div>

          <div class="card-actions">
            <button
              class="btn-delete"
              type="button"
              (click)="confirmarExclusao(c)"
              [disabled]="deletando === c.numero"
              [attr.aria-label]="'Excluir conta número ' + c.numero + ' de ' + c.clienteNome"
            >
              <mat-icon aria-hidden="true">delete</mat-icon>
              <span>{{ deletando === c.numero ? 'Excluindo...' : 'Excluir conta' }}</span>
            </button>
          </div>
        </article>
      </div>
    </div>
  `,
  styles: [`
    .loading, .empty {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;
      padding: 64px 0;
      color: var(--text-secondary);
    }

    .empty mat-icon {
      font-size: 48px;
      width: 48px;
      height: 48px;
    }

    .spin {
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      to { transform: rotate(360deg); }
    }

    .card-grid {
      display: grid;
      gap: 20px;
      grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    }

    .conta-card {
      padding: 20px;
      border-left: 4px solid var(--primary);
      transition: transform var(--transition), box-shadow var(--transition);
    }

    .conta-card:hover {
      box-shadow: var(--shadow-md);
      transform: translateY(-2px);
    }

    .conta-card.poupanca {
      border-left-color: var(--success);
    }

    .card-top {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;
    }

    .tipo-badge {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 6px 12px;
      border-radius: 999px;
      font-size: 12px;
      font-weight: 650;
      text-transform: uppercase;
      letter-spacing: 0.4px;
    }

    .tipo-badge mat-icon {
      font-size: 16px;
      width: 16px;
      height: 16px;
    }

    .tipo-badge.corrente {
      background: var(--primary-soft);
      color: var(--primary);
    }

    .tipo-badge.poupanca {
      background: var(--success-soft);
      color: var(--success-text);
    }

    .numero {
      font-size: 20px;
      font-weight: 750;
      color: var(--text-primary);
      letter-spacing: -0.01em;
    }

    .card-body {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding-bottom: 16px;
      border-bottom: 1px solid var(--border-color);
    }

    .info-row {
      display: flex;
      align-items: flex-start;
      gap: 10px;
      font-size: 14px;
      color: var(--text-primary);
      min-width: 0;
    }

    .info-row mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
      color: var(--text-secondary);
      flex-shrink: 0;
      margin-top: 1px;
    }

    .cpf {
      color: var(--text-secondary);
    }

    .card-actions {
      padding-top: 16px;
    }

    .btn-delete {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 9px 16px;
      border: 1px solid var(--danger-border);
      background: var(--danger-soft);
      color: var(--danger);
      border-radius: 10px;
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      transition: background var(--transition), border-color var(--transition);
      font-family: inherit;
      width: 100%;
      justify-content: center;
    }

    .btn-delete:hover:not(:disabled) {
      background: var(--danger);
      border-color: var(--danger);
      color: #ffffff;
    }

    .btn-delete:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .btn-delete mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
    }

    @media (max-width: 480px) {
      .card-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class ListarContasComponent implements OnInit {
  contas: ContaResponse[] = [];
  loading = false;
  deletando: number | null = null;

  constructor(
    private service: ContaService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.loading = true;
    this.service.listar().subscribe({
      next: (res) => this.contas = res,
      error: () => {
        this.snackBar.open('Erro ao carregar contas', 'Fechar', { duration: 3000, panelClass: 'snack-error' });
        this.loading = false;
      },
      complete: () => this.loading = false
    });
  }

  confirmarExclusao(conta: ContaResponse): void {
    if (conta.saldo !== 0) {
      this.snackBar.open(
        `Não é possível excluir: saldo de R$ ${conta.saldo.toFixed(2)} deve ser zero.`,
        'Fechar',
        { duration: 5000, panelClass: 'snack-error' }
      );
      return;
    }

    const confirmar = confirm(`Tem certeza que deseja excluir a conta #${conta.numero} de ${conta.clienteNome}?`);
    if (!confirmar) return;

    this.deletando = conta.numero;
    this.service.deletar(conta.numero).subscribe({
      next: () => {
        this.snackBar.open('Conta removida com sucesso!', 'Fechar', { duration: 3000, panelClass: 'snack-success' });
        this.carregar();
      },
      error: (err) => {
        const msg = err.error || 'Erro ao excluir conta';
        this.snackBar.open(msg, 'Fechar', { duration: 5000, panelClass: 'snack-error' });
        this.deletando = null;
      },
      complete: () => this.deletando = null
    });
  }
}
