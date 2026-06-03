import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BudgetService } from '../../core/services/budget.service';
import { Budget, Category } from '../../core/models';

@Component({
  selector: 'app-budget-form',
  standalone: true,
  imports: [
    ReactiveFormsModule, NgFor, NgIf,
    MatDialogModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatSnackBarModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data.budget ? 'Editar' : 'Novo' }} Orçamento</h2>
    <mat-dialog-content>
      <form [formGroup]="form" style="display:flex;flex-direction:column;gap:4px;padding-top:8px;min-width:380px">
        <mat-form-field appearance="outline" class="full-width" *ngIf="!data.budget">
          <mat-label>Categoria (apenas EXPENSE)</mat-label>
          <mat-select formControlName="categoryId">
            <mat-option *ngFor="let c of expenseCategories" [value]="c.id">{{ c.name }}</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Limite (R$)</mat-label>
          <input matInput type="number" step="0.01" formControlName="amount">
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button color="primary" (click)="submit()" [disabled]="loading">Salvar</button>
    </mat-dialog-actions>
  `
})
export class BudgetFormComponent implements OnInit {
  form = this.fb.group({
    categoryId: [''],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]]
  });
  loading = false;
  get expenseCategories() { return this.data.categories.filter(c => c.type === 'EXPENSE'); }

  constructor(
    private fb: FormBuilder,
    private svc: BudgetService,
    private snack: MatSnackBar,
    public dialogRef: MatDialogRef<BudgetFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { budget?: Budget; month: string; categories: Category[] }
  ) {}

  ngOnInit() {
    if (this.data.budget) this.form.patchValue({ amount: this.data.budget.amount });
    else this.form.get('categoryId')?.addValidators(Validators.required);
  }

  submit() {
    if (this.form.invalid) return;
    this.loading = true;
    const val = this.form.value;
    const obs = this.data.budget
      ? this.svc.update(this.data.budget.id, val.amount!)
      : this.svc.create({ categoryId: val.categoryId!, yearMonth: this.data.month, amount: val.amount! });
    obs.subscribe({
      next: () => { this.snack.open('Salvo!', 'OK', { duration: 2000, panelClass: 'success-snack' }); this.dialogRef.close(true); },
      error: err => { this.snack.open(err.error?.errors?.[0] ?? 'Erro', 'OK', { duration: 3000, panelClass: 'error-snack' }); this.loading = false; }
    });
  }
}
