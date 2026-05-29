import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { ContaService } from '../services/conta.service';

@Component({
  selector: 'app-operacoes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatCardModule, MatIconModule, MatSnackBarModule],
  template: `
    <h2>Operações</h2>

    <div class="op-grid">
      <mat-card appearance="outlined" class="op-card">
        <mat-card-header>
          <mat-icon mat-card-avatar>payments</mat-icon>
          <mat-card-title>Depositar</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <form [formGroup]="formDeposito" (ngSubmit)="onDepositar()">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Número da Conta</mat-label>
              <input matInput type="number" formControlName="numero" required>
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Valor</mat-label>
              <input matInput type="number" formControlName="valor" step="0.01" required>
            </mat-form-field>
            <button mat-raised-button color="primary" type="submit" [disabled]="formDeposito.invalid || loadingDep">
              {{ loadingDep ? 'Depositando...' : 'Depositar' }}
            </button>
          </form>
        </mat-card-content>
      </mat-card>

      <mat-card appearance="outlined" class="op-card">
        <mat-card-header>
          <mat-icon mat-card-avatar>money_off</mat-icon>
          <mat-card-title>Sacar</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <form [formGroup]="formSaque" (ngSubmit)="onSacar()">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Número da Conta</mat-label>
              <input matInput type="number" formControlName="numero" required>
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Valor</mat-label>
              <input matInput type="number" formControlName="valor" step="0.01" required>
            </mat-form-field>
            <button mat-raised-button color="warn" type="submit" [disabled]="formSaque.invalid || loadingSaq">
              {{ loadingSaq ? 'Sacando...' : 'Sacar' }}
            </button>
          </form>
        </mat-card-content>
      </mat-card>

      <mat-card appearance="outlined" class="op-card op-card-wide">
        <mat-card-header>
          <mat-icon mat-card-avatar>swap_horiz</mat-icon>
          <mat-card-title>Transferir</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <form [formGroup]="formTransferencia" (ngSubmit)="onTransferir()">
            <div class="transfer-fields">
              <mat-form-field appearance="outline">
                <mat-label>Conta Origem</mat-label>
                <input matInput type="number" formControlName="origem" required>
              </mat-form-field>
              <mat-icon class="arrow">arrow_forward</mat-icon>
              <mat-form-field appearance="outline">
                <mat-label>Conta Destino</mat-label>
                <input matInput type="number" formControlName="destino" required>
              </mat-form-field>
            </div>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Valor</mat-label>
              <input matInput type="number" formControlName="valor" step="0.01" required>
            </mat-form-field>
            <button mat-raised-button color="primary" type="submit" [disabled]="formTransferencia.invalid || loadingTransf">
              {{ loadingTransf ? 'Transferindo...' : 'Transferir' }}
            </button>
          </form>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    h2 {
      margin: 0 0 20px;
      color: #3f51b5;
      font-weight: 500;
    }

    .op-grid {
      display: grid;
      gap: 20px;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    }

    .op-card-wide {
      grid-column: 1 / -1;
    }

    .full-width {
      width: 100%;
      margin-bottom: 8px;
    }

    .transfer-fields {
      display: flex;
      align-items: flex-start;
      gap: 8px;
    }

    .transfer-fields mat-form-field {
      flex: 1;
    }

    .arrow {
      margin-top: 20px;
      color: #3f51b5;
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
