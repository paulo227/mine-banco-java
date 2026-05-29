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
    <div class="page-container">
      <div class="page-header">
        <div>
          <h1>Contas Cadastradas</h1>
          <p class="page-subtitle">{{ contas.length }} conta(s) encontrada(s)</p>
        </div>
        <button mat-stroked-button color="primary" (click)="carregar()" [disabled]="loading">
          <mat-icon>refresh</mat-icon>
          Atualizar
        </button>
      </div>

      <div *ngIf="loading" class="loading">
        <mat-icon class="spin">refresh</mat-icon>
        Carregando...
      </div>

      <div *ngIf="!loading && contas.length === 0" class="empty">
        <mat-icon>account_balance_wallet</mat-icon>
        <p>Nenhuma conta cadastrada.</p>
      </div>

      <div class="card-grid" *ngIf="!loading">
        <div *ngFor="let c of contas" class="conta-card" [class.poupanca]="c.tipo === 'POUPANCA'">
          <div class="card-top">
            <span class="tipo-badge" [class.corrente]="c.tipo === 'CORRENTE'" [class.poupanca]="c.tipo === 'POUPANCA'">
              <mat-icon>{{ c.tipo === 'CORRENTE' ? 'account_balance' : 'savings' }}</mat-icon>
              {{ c.tipo }}
            </span>
            <span class="numero">#{{ c.numero }}</span>
          </div>

          <div class="card-body">
            <div class="info-row">
              <mat-icon>person</mat-icon>
              <span>{{ c.clienteNome }} (CPF: {{ c.clienteCpf }})</span>
            </div>
            <div class="info-row">
              <mat-icon>payments</mat-icon>
              <span><strong>Saldo:</strong> R$ {{ c.saldo | number:'1.2-2' }}</span>
            </div>
            <div class="info-row" *ngIf="c.tipo === 'CORRENTE'">
              <mat-icon>credit_card</mat-icon>
              <span><strong>Limite:</strong> R$ {{ c.limite | number:'1.2-2' }}</span>
            </div>
            <div class="info-row">
              <mat-icon>receipt</mat-icon>
              <span><strong>Imposto:</strong> R$ {{ c.imposto | number:'1.2-2' }}</span>
            </div>
          </div>

          <div class="card-actions">
            <button class="btn-delete" (click)="confirmarExclusao(c)" [disabled]="deletando === c.numero">
              <mat-icon>delete</mat-icon>
              <span>{{ deletando === c.numero ? 'Excluindo...' : 'Excluir' }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 24px;
      max-width: 1200px;
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
    }

    .page-header h1 {
      margin: 0;
      font-size: 22px;
      font-weight: 700;
      color: var(--text-primary);
    }

    .page-subtitle {
      margin: 4px 0 0;
      font-size: 14px;
      color: var(--text-secondary);
    }

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
      grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
    }

    .conta-card {
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 16px;
      padding: 20px;
      border-left: 4px solid var(--primary);
      transition: all 0.3s ease;
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
      margin-bottom: 16px;
    }

    .tipo-badge {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 6px 14px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .tipo-badge mat-icon {
      font-size: 16px;
      width: 16px;
      height: 16px;
    }

    .tipo-badge.corrente {
      background: #eef2ff;
      color: var(--primary);
    }

    .tipo-badge.poupanca {
      background: #ecfdf5;
      color: var(--success);
    }

    .numero {
      font-size: 20px;
      font-weight: 700;
      color: var(--text-primary);
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
      align-items: center;
      gap: 10px;
      font-size: 14px;
      color: var(--text-primary);
    }

    .info-row mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
      color: var(--text-secondary);
    }

    .card-actions {
      padding-top: 16px;
    }

    .btn-delete {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      border: 1px solid #fecaca;
      background: #fef2f2;
      color: var(--danger);
      border-radius: 10px;
      font-size: 13px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s;
      font-family: inherit;
      width: 100%;
      justify-content: center;
    }

    .btn-delete:hover:not(:disabled) {
      background: #fee2e2;
      border-color: var(--danger);
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
