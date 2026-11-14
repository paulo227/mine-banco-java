import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ContaService } from '../services/conta.service';

@Component({
  selector: 'app-criar-conta',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  template: `
    <div class="page">
      <header class="page-header">
        <div>
          <h1 class="page-title">Criar nova conta</h1>
          <p class="page-subtitle">Cadastre o cliente e defina o tipo de conta.</p>
        </div>
      </header>

      <section class="form-card surface-card" aria-labelledby="criar-conta-titulo">
        <h2 id="criar-conta-titulo" class="card-title">Dados da conta</h2>

        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form-grid" novalidate>
          <mat-form-field appearance="outline" class="span-2">
            <mat-label>Nome do cliente</mat-label>
            <input matInput formControlName="nome" placeholder="Nome completo" autocomplete="name" required>
            <mat-error *ngIf="form.get('nome')?.hasError('required')">Informe o nome do cliente</mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>CPF</mat-label>
            <input
              matInput
              formControlName="cpf"
              placeholder="Apenas números"
              inputmode="numeric"
              pattern="[0-9]*"
              maxlength="11"
              autocomplete="off"
              required
            >
            <mat-hint>Somente números</mat-hint>
            <mat-error *ngIf="form.get('cpf')?.hasError('required')">CPF é obrigatório</mat-error>
            <mat-error *ngIf="form.get('cpf')?.hasError('minlength')">CPF deve ter 11 dígitos</mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Número da conta</mat-label>
            <input matInput type="number" formControlName="numero" inputmode="numeric" min="1" required>
            <mat-error *ngIf="form.get('numero')?.hasError('required')">Número é obrigatório</mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Tipo de conta</mat-label>
            <mat-select formControlName="tipo">
              <mat-option value="CORRENTE">Corrente</mat-option>
              <mat-option value="POUPANCA">Poupança</mat-option>
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline" *ngIf="isCorrente">
            <mat-label>Limite do cheque especial</mat-label>
            <input matInput type="number" formControlName="limite" step="0.01" min="0" inputmode="decimal">
            <mat-hint>Em reais (R$)</mat-hint>
          </mat-form-field>

          <div class="form-actions span-2">
            <button mat-flat-button color="primary" type="submit" [disabled]="form.invalid || loading">
              {{ loading ? 'Criando...' : 'Criar conta' }}
            </button>
            <a mat-stroked-button routerLink="/listar-contas">Ver contas</a>
          </div>
        </form>
      </section>
    </div>
  `,
  styles: [`
    .form-card {
      padding: 24px;
      max-width: 780px;
    }

    .card-title {
      margin: 0 0 20px;
      font-size: 16px;
      font-weight: 650;
      color: var(--text-primary);
    }

    .form-grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 4px 20px;
    }

    .form-grid mat-form-field {
      width: 100%;
    }

    .span-2 {
      grid-column: 1 / -1;
    }

    .form-actions {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      gap: 12px;
      margin-top: 8px;
    }

    .form-actions button,
    .form-actions a {
      min-height: 44px;
      padding: 0 22px;
    }

    @media (max-width: 620px) {
      .form-card {
        padding: 18px;
      }

      .form-grid {
        grid-template-columns: 1fr;
      }

      .form-actions button,
      .form-actions a {
        width: 100%;
      }
    }
  `]
})
export class CriarContaComponent {
  form: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private service: ContaService,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      nome: ['', Validators.required],
      cpf: ['', [Validators.required, Validators.minLength(11)]],
      numero: [null, Validators.required],
      tipo: ['CORRENTE'],
      limite: [0]
    });
  }

  get isCorrente(): boolean {
    return this.form.get('tipo')?.value === 'CORRENTE';
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading = true;
    this.service.criar(this.form.value).subscribe({
      next: (res) => {
        this.snackBar.open(`Conta ${res.tipo} Nº ${res.numero} criada com sucesso!`, 'Fechar', { duration: 4000, panelClass: 'snack-success' });
        this.form.reset({ tipo: 'CORRENTE', limite: 0 });
      },
      error: (err) => {
        this.snackBar.open(err.error || 'Erro ao criar conta', 'Fechar', { duration: 4000, panelClass: 'snack-error' });
        this.loading = false;
      },
      complete: () => this.loading = false
    });
  }
}
