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
  selector: 'app-relatorios',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatCardModule, MatIconModule, MatSnackBarModule],
  template: `
    <h2>Relatórios</h2>

    <div class="rel-grid">
      <mat-card appearance="outlined">
        <mat-card-header>
          <mat-icon mat-card-avatar>trending_up</mat-icon>
          <mat-card-title>Aplicar Rendimento (Poupança)</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <form [formGroup]="formRendimento" (ngSubmit)="onAplicarRendimento()">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Taxa (ex: 0.01 = 1%)</mat-label>
              <input matInput type="number" formControlName="taxa" step="0.0001" required>
              <mat-hint>Percentual em decimal. Ex: 0.01 para 1%</mat-hint>
            </mat-form-field>
            <button mat-raised-button color="primary" type="submit" [disabled]="formRendimento.invalid || loadingRend">
              {{ loadingRend ? 'Aplicando...' : 'Aplicar Rendimento' }}
            </button>
          </form>
        </mat-card-content>
      </mat-card>

      <mat-card appearance="outlined">
        <mat-card-header>
          <mat-icon mat-card-avatar>receipt_long</mat-icon>
          <mat-card-title>Total de Impostos</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <button mat-raised-button color="accent" (click)="onCalcularImpostos()" [disabled]="loadingImp">
            <mat-icon>calculate</mat-icon>
            {{ loadingImp ? 'Calculando...' : 'Calcular' }}
          </button>
          <div class="imposto-result" *ngIf="totalImpostos !== null">
            <span class="label">Total de Impostos Arrecadados:</span>
            <span class="valor">R$ {{ totalImpostos | number:'1.2-2' }}</span>
          </div>
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

    .rel-grid {
      display: grid;
      gap: 20px;
      grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
    }

    .full-width {
      width: 100%;
      margin-bottom: 8px;
    }

    .imposto-result {
      margin-top: 20px;
      padding: 16px;
      background: #e8f5e9;
      border-radius: 8px;
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .label {
      font-size: 13px;
      color: #555;
    }

    .valor {
      font-size: 28px;
      font-weight: 700;
      color: #2e7d32;
    }
  `]
})
export class RelatoriosComponent {
  formRendimento: FormGroup;
  loadingRend = false;
  loadingImp = false;
  totalImpostos: number | null = null;

  constructor(
    private fb: FormBuilder,
    private service: ContaService,
    private snackBar: MatSnackBar
  ) {
    this.formRendimento = this.fb.group({ taxa: [null, Validators.required] });
  }

  onAplicarRendimento(): void {
    if (this.formRendimento.invalid) return;
    this.loadingRend = true;
    this.service.aplicarRendimento(this.formRendimento.value).subscribe({
      next: () => {
        this.snackBar.open('Rendimento aplicado com sucesso!', 'Fechar', { duration: 4000, panelClass: 'snack-success' });
        this.formRendimento.reset();
      },
      error: (err) => {
        this.snackBar.open(err.error || 'Erro ao aplicar rendimento', 'Fechar', { duration: 4000, panelClass: 'snack-error' });
        this.loadingRend = false;
      },
      complete: () => this.loadingRend = false
    });
  }

  onCalcularImpostos(): void {
    this.loadingImp = true;
    this.service.calcularImpostos().subscribe({
      next: (res) => this.totalImpostos = res.totalImpostos,
      error: () => {
        this.snackBar.open('Erro ao calcular impostos', 'Fechar', { duration: 3000, panelClass: 'snack-error' });
        this.loadingImp = false;
      },
      complete: () => this.loadingImp = false
    });
  }
}
