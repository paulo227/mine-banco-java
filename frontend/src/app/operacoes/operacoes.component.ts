import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { ContaService } from '../services/conta.service';

@Component({
  selector: 'app-operacoes',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ],
  template: `
    <div class="page">
      <header class="page-header">
        <div>
          <h1 class="page-title">Operações</h1>
          <p class="page-subtitle">Deposite, saque e transfira valores entre contas.</p>
        </div>
      </header>

      <div class="op-grid">
        <section class="op-card surface-card" aria-labelledby="deposito-title">
          <div class="op-card-header">
            <span class="op-icon deposit" aria-hidden="true">
              <mat-icon aria-hidden="true">payments</mat-icon>
            </span>
            <div class="op-title-wrap">
              <h2 id="deposito-title">Depositar</h2>
              <p class="op-desc">Adicione fundos a uma conta.</p>
            </div>
          </div>

          <form [formGroup]="formDeposito" (ngSubmit)="onDepositar()" novalidate>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Número da conta</mat-label>
              <input matInput type="number" formControlName="numero" inputmode="numeric" min="1" required>
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Valor</mat-label>
              <span matPrefix>R$&nbsp;</span>
              <input matInput type="number" formControlName="valor" step="0.01" min="0.01" inputmode="decimal" required>
            </mat-form-field>
            <button mat-flat-button color="primary" type="submit" class="submit-btn" [disabled]="formDeposito.invalid || loadingDep">
              {{ loadingDep ? 'Depositando...' : 'Depositar' }}
            </button>
          </form>
        </section>

        <section class="op-card surface-card" aria-labelledby="saque-title">
          <div class="op-card-header">
            <span class="op-icon withdraw" aria-hidden="true">
              <mat-icon aria-hidden="true">money_off</mat-icon>
            </span>
            <div class="op-title-wrap">
              <h2 id="saque-title">Sacar</h2>
              <p class="op-desc">Retire valores de uma conta.</p>
            </div>
          </div>

          <form [formGroup]="formSaque" (ngSubmit)="onSacar()" novalidate>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Número da conta</mat-label>
              <input matInput type="number" formControlName="numero" inputmode="numeric" min="1" required>
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Valor</mat-label>
              <span matPrefix>R$&nbsp;</span>
              <input matInput type="number" formControlName="valor" step="0.01" min="0.01" inputmode="decimal" required>
            </mat-form-field>
            <button mat-flat-button color="warn" type="submit" class="submit-btn" [disabled]="formSaque.invalid || loadingSaq">
              {{ loadingSaq ? 'Sacando...' : 'Sacar' }}
            </button>
          </form>
        </section>

        <section class="op-card op-card-wide surface-card" aria-labelledby="transferencia-title">
          <div class="op-card-header">
            <span class="op-icon transfer" aria-hidden="true">
              <mat-icon aria-hidden="true">swap_horiz</mat-icon>
            </span>
            <div class="op-title-wrap">
              <h2 id="transferencia-title">Transferir</h2>
              <p class="op-desc">Mova valores entre duas contas.</p>
            </div>
          </div>

          <form [formGroup]="formTransferencia" (ngSubmit)="onTransferir()" novalidate>
            <div class="transfer-fields">
              <mat-form-field appearance="outline">
                <mat-label>Conta de origem</mat-label>
                <input matInput type="number" formControlName="origem" inputmode="numeric" min="1" required>
              </mat-form-field>
              <mat-icon class="arrow" aria-hidden="true">arrow_forward</mat-icon>
              <mat-form-field appearance="outline">
                <mat-label>Conta de destino</mat-label>
                <input matInput type="number" formControlName="destino" inputmode="numeric" min="1" required>
              </mat-form-field>
            </div>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Valor</mat-label>
              <span matPrefix>R$&nbsp;</span>
              <input matInput type="number" formControlName="valor" step="0.01" min="0.01" inputmode="decimal" required>
            </mat-form-field>
            <button mat-flat-button color="primary" type="submit" class="submit-btn" [disabled]="formTransferencia.invalid || loadingTransf">
              {{ loadingTransf ? 'Transferindo...' : 'Transferir' }}
            </button>
          </form>
        </section>
      </div>
    </div>
  `,
  styles: [`
    .op-grid {
      display: grid;
      gap: 20px;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    }

    .op-card {
      padding: 22px;
    }

    .op-card-wide {
      grid-column: 1 / -1;
    }

    .op-card-header {
      display: flex;
      align-items: flex-start;
      gap: 14px;
      margin-bottom: 18px;
    }

    .op-icon {
      width: 44px;
      height: 44px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .op-icon mat-icon {
      font-size: 24px;
      width: 24px;
      height: 24px;
    }

    .op-icon.deposit {
      background: var(--success-soft);
      color: var(--success-text);
    }

    .op-icon.withdraw {
      background: var(--danger-soft);
      color: var(--danger);
    }

    .op-icon.transfer {
      background: var(--primary-soft);
      color: var(--primary);
    }

    .op-title-wrap h2 {
      margin: 0;
      font-size: 16px;
      font-weight: 650;
      color: var(--text-primary);
    }

    .op-desc {
      margin: 2px 0 0;
      font-size: 13px;
      color: var(--text-secondary);
    }

    .full-width {
      width: 100%;
      margin-bottom: 4px;
    }

    .transfer-fields {
      display: grid;
      grid-template-columns: 1fr auto 1fr;
      gap: 12px;
      align-items: start;
    }

    .arrow {
      margin-top: 18px;
      color: var(--text-secondary);
    }

    .submit-btn {
      min-height: 44px;
      padding: 0 24px;
      margin-top: 8px;
    }

    @media (max-width: 640px) {
      .op-card {
        padding: 18px;
      }

      .transfer-fields {
        grid-template-columns: 1fr;
      }

      .arrow {
        margin: 0 auto;
        transform: rotate(90deg);
      }

      .submit-btn {
        width: 100%;
      }
    }
  `]
})
export class OperacoesComponent {
  formDeposito: FormGroup;
  formSaque: FormGroup;
  formTransferencia: FormGroup;

  loadingDep = false;
  loadingSaq = false;
  loadingTransf = false;

  constructor(
    private fb: FormBuilder,
    private service: ContaService,
    private snackBar: MatSnackBar
  ) {
    this.formDeposito = this.fb.group({ numero: [null, Validators.required], valor: [null, Validators.required] });
    this.formSaque = this.fb.group({ numero: [null, Validators.required], valor: [null, Validators.required] });
    this.formTransferencia = this.fb.group({ origem: [null, Validators.required], destino: [null, Validators.required], valor: [null, Validators.required] });
  }

  onDepositar(): void {
    if (this.formDeposito.invalid) return;
    this.loadingDep = true;
    const { numero, valor } = this.formDeposito.value;
    this.service.depositar(numero, { numero, valor }).subscribe({
      next: (res) => {
        this.snackBar.open(`Depósito realizado! Saldo: R$ ${res.saldo.toFixed(2)}`, 'Fechar', { duration: 4000, panelClass: 'snack-success' });
        this.formDeposito.reset();
      },
      error: (err) => this.showError(err),
      complete: () => this.loadingDep = false
    });
  }

  onSacar(): void {
    if (this.formSaque.invalid) return;
    this.loadingSaq = true;
    const { numero, valor } = this.formSaque.value;
    this.service.sacar(numero, { numero, valor }).subscribe({
      next: (res) => {
        this.snackBar.open(`Saque realizado! Saldo: R$ ${res.saldo.toFixed(2)}`, 'Fechar', { duration: 4000, panelClass: 'snack-success' });
        this.formSaque.reset();
      },
      error: (err) => this.showError(err),
      complete: () => this.loadingSaq = false
    });
  }

  onTransferir(): void {
    if (this.formTransferencia.invalid) return;
    this.loadingTransf = true;
    this.service.transferir(this.formTransferencia.value).subscribe({
      next: (res) => {
        this.snackBar.open(`Transferência realizada! Origem: R$ ${res[0].saldo.toFixed(2)} | Destino: R$ ${res[1].saldo.toFixed(2)}`, 'Fechar', { duration: 5000, panelClass: 'snack-success' });
        this.formTransferencia.reset();
      },
      error: (err) => this.showError(err),
      complete: () => this.loadingTransf = false
    });
  }

  private showError(err: any): void {
    this.snackBar.open(err.error || 'Erro na operação', 'Fechar', { duration: 4000, panelClass: 'snack-error' });
    this.loadingDep = false;
    this.loadingSaq = false;
    this.loadingTransf = false;
  }
}
