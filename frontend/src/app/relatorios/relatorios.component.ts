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
  selector: 'app-relatorios',
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
          <h1 class="page-title">Relatórios</h1>
          <p class="page-subtitle">Aplique rendimento e acompanhe os impostos arrecadados.</p>
        </div>
      </header>

      <div class="rel-grid">
        <section class="rel-card surface-card" aria-labelledby="rendimento-title">
          <div class="rel-card-header">
            <span class="rel-icon success" aria-hidden="true">
              <mat-icon aria-hidden="true">trending_up</mat-icon>
            </span>
            <div class="rel-title-wrap">
              <h2 id="rendimento-title">Aplicar rendimento</h2>
              <p class="rel-desc">Rendimento para contas poupança.</p>
            </div>
          </div>

          <form [formGroup]="formRendimento" (ngSubmit)="onAplicarRendimento()" novalidate>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Taxa</mat-label>
              <input matInput type="number" formControlName="taxa" step="0.0001" min="0" inputmode="decimal" required>
              <mat-hint>Percentual em decimal. Ex.: 0,01 para 1%</mat-hint>
            </mat-form-field>
            <button mat-flat-button color="primary" type="submit" class="submit-btn" [disabled]="formRendimento.invalid || loadingRend">
              {{ loadingRend ? 'Aplicando...' : 'Aplicar rendimento' }}
            </button>
          </form>
        </section>

        <section class="rel-card surface-card" aria-labelledby="impostos-title">
          <div class="rel-card-header">
            <span class="rel-icon warning" aria-hidden="true">
              <mat-icon aria-hidden="true">receipt_long</mat-icon>
            </span>
            <div class="rel-title-wrap">
              <h2 id="impostos-title">Total de impostos</h2>
              <p class="rel-desc">Soma dos impostos de todas as contas.</p>
            </div>
          </div>

          <button mat-flat-button color="accent" type="button" class="submit-btn" (click)="onCalcularImpostos()" [disabled]="loadingImp">
            <mat-icon aria-hidden="true">calculate</mat-icon>
            {{ loadingImp ? 'Calculando...' : 'Calcular impostos' }}
          </button>

          <div class="imposto-result" *ngIf="totalImpostos !== null" role="status" aria-live="polite">
            <span class="label">Total de impostos arrecadados</span>
            <span class="valor">{{ totalImpostos | currency:'BRL' }}</span>
          </div>
        </section>
      </div>
    </div>
  `,
  styles: [`
    .rel-grid {
      display: grid;
      gap: 20px;
      grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    }

    .rel-card {
      padding: 22px;
    }

    .rel-card-header {
      display: flex;
      align-items: flex-start;
      gap: 14px;
      margin-bottom: 18px;
    }

    .rel-icon {
      width: 44px;
      height: 44px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .rel-icon mat-icon {
      font-size: 24px;
      width: 24px;
      height: 24px;
    }

    .rel-icon.success {
      background: var(--success-soft);
      color: var(--success-text);
    }

    .rel-icon.warning {
      background: var(--warning-soft);
      color: var(--warning-text);
    }

    .rel-title-wrap h2 {
      margin: 0;
      font-size: 16px;
      font-weight: 650;
      color: var(--text-primary);
    }

    .rel-desc {
      margin: 2px 0 0;
      font-size: 13px;
      color: var(--text-secondary);
    }

    .full-width {
      width: 100%;
      margin-bottom: 4px;
    }

    .submit-btn {
      min-height: 44px;
      padding: 0 24px;
      margin-top: 8px;
    }

    .imposto-result {
      margin-top: 20px;
      padding: 16px 18px;
      background: var(--success-soft);
      border: 1px solid var(--success);
      border-radius: 12px;
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .label {
      font-size: 13px;
      color: var(--text-secondary);
    }

    .valor {
      font-size: 28px;
      font-weight: 750;
      color: var(--success-text);
      letter-spacing: -0.02em;
    }

    @media (max-width: 640px) {
      .rel-card {
        padding: 18px;
      }

      .submit-btn {
        width: 100%;
      }
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
