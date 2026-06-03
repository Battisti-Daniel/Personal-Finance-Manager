import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { TransactionService } from '../../core/services/transaction.service';
import { Category, Transaction } from '../../core/models';

@Component({
  selector: 'app-transaction-form',
  standalone: true,
  imports: [
    ReactiveFormsModule, NgFor, NgIf,
    MatDialogModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatSnackBarModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data.transaction ? 'Editar' : 'Nova' }} Transação</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="form-grid">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Descrição</mat-label>
          <input matInput formControlName="description">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Valor</mat-label>
          <input matInput type="number" step="0.01" formControlName="amount">
          <span matTextPrefix>R$&nbsp;</span>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Data</mat-label>
          <input matInput type="date" formControlName="date">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width" *ngIf="!data.transaction">
          <mat-label>Categoria</mat-label>
          <mat-select formControlName="categoryId">
            <mat-option *ngFor="let c of data.categories" [value]="c.id">
              {{ c.name }} ({{ c.type === 'INCOME' ? 'Receita' : 'Despesa' }})
            </mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Notas (opcional)</mat-label>
          <textarea matInput formControlName="notes" rows="2"></textarea>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button color="primary" (click)="submit()" [disabled]="loading">
        {{ loading ? 'Salvando...' : 'Salvar' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`.form-grid { display: flex; flex-direction: column; gap: 4px; min-width: 400px; padding-top: 8px; }`]
})
export class TransactionFormComponent implements OnInit {
  form = this.fb.group({
    description: ['', Validators.required],
    amount:      [null as number | null, [Validators.required, Validators.min(0.01)]],
    date:        ['', Validators.required],
    categoryId:  [''],
    notes:       ['']
  });
  loading = false;

  constructor(
    private fb: FormBuilder,
    private svc: TransactionService,
    private snack: MatSnackBar,
    public dialogRef: MatDialogRef<TransactionFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { transaction?: Transaction; categories: Category[] }
  ) {}

  ngOnInit() {
    if (this.data.transaction) {
      const t = this.data.transaction;
      this.form.patchValue({ description: t.description, amount: t.amount, date: t.date, notes: t.notes });
    } else {
      this.form.get('categoryId')?.addValidators(Validators.required);
    }
  }

  submit() {
    if (this.form.invalid) return;
    this.loading = true;
    const val = this.form.value;
    const obs = this.data.transaction
      ? this.svc.update(this.data.transaction.id, { description: val.description!, amount: val.amount!, date: val.date!, notes: val.notes || undefined })
      : this.svc.create({ categoryId: val.categoryId!, description: val.description!, amount: val.amount!, date: val.date!, notes: val.notes || undefined });

    obs.subscribe({
      next: () => { this.snack.open('Salvo!', 'OK', { duration: 2000, panelClass: 'success-snack' }); this.dialogRef.close(true); },
      error: err => { this.snack.open(err.error?.errors?.[0] ?? 'Erro ao salvar', 'OK', { duration: 3000, panelClass: 'error-snack' }); this.loading = false; }
    });
  }
}
