import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ContaService } from '../services/conta.service';

@Component({
  selector: 'app-criar-conta',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatSnackBarModule],
  template: `
    <h2>Criar Nova Conta</h2>

    <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form-grid">
      <mat-form-field appearance="outline">
        <mat-label>Nome do Cliente</mat-label>
        <input matInput formControlName="nome" placeholder="Nome completo" required>
        <mat-error *ngIf="form.get('nome')?.hasError('required')">Nome é obrigatório</mat-error>
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>CPF</mat-label>
        <input matInput formControlName="cpf" placeholder="Apenas números" maxlength="11" required>
        <mat-error *ngIf="form.get('cpf')?.hasError('required')">CPF é obrigatório</mat-error>
        <mat-error *ngIf="form.get('cpf')?.hasError('minlength')">CPF deve ter 11 dígitos</mat-error>
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Número da Conta</mat-label>
        <input matInput type="number" formControlName="numero" required>
        <mat-error *ngIf="form.get('numero')?.hasError('required')">Número é obrigatório</mat-error>
      </mat-form-field>

      <mat-form-field appearance="outline">
        <mat-label>Tipo de Conta</mat-label>
        <mat-select formControlName="tipo">
          <mat-option [value]="1">Corrente</mat-option>
          <mat-option [value]="2">Poupança</mat-option>
        </mat-select>
      </mat-form-field>

      <mat-form-field appearance="outline" *ngIf="isCorrente">
        <mat-label>Limite Cheque Especial</mat-label>
        <input matInput type="number" formControlName="limite" step="0.01">
      </mat-form-field>

      <div class="form-actions">
        <button mat-raised-button color="primary" type="submit" [disabled]="form.invalid || loading">
          {{ loading ? 'Criando...' : 'Criar Conta' }}
        </button>
      </div>
    </form>
  `,
  styles: [`
    h2 {
      margin: 0 0 20px;
      color: #3f51b5;
      font-weight: 500;
    }

    .form-grid {
      display: flex;
      flex-direction: column;
      gap: 16px;
      max-width: 480px;
    }

    .form-actions {
      margin-top: 8px;
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
      tipo: [1],
      limite: [0]
    });
  }

  get isCorrente(): boolean {
    return this.form.get('tipo')?.value === 1;
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loading = true;
    this.service.criar(this.form.value).subscribe({
      next: (res) => {
        this.snackBar.open(`Conta ${res.tipo} Nº ${res.numero} criada com sucesso!`, 'Fechar', { duration: 4000, panelClass: 'snack-success' });
        this.form.reset({ tipo: 1, limite: 0 });
      },
      error: (err) => {
        this.snackBar.open(err.error || 'Erro ao criar conta', 'Fechar', { duration: 4000, panelClass: 'snack-error' });
        this.loading = false;
      },
      complete: () => this.loading = false
    });
  }
}
