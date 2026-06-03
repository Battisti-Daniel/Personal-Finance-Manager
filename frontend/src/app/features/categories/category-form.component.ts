import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { NgFor } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CategoryService } from '../../core/services/category.service';
import { Category } from '../../core/models';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [
    ReactiveFormsModule, NgFor,
    MatDialogModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatSnackBarModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Editar' : 'Nova' }} Categoria</h2>
    <mat-dialog-content>
      <form [formGroup]="form" style="display:flex;flex-direction:column;gap:4px;padding-top:8px;min-width:380px">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome</mat-label>
          <input matInput formControlName="name">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Tipo</mat-label>
          <mat-select formControlName="type">
            <mat-option value="EXPENSE">Despesa</mat-option>
            <mat-option value="INCOME">Receita</mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Cor (hex)</mat-label>
          <input matInput formControlName="color" placeholder="#6366f1">
          <input type="color" matSuffix [value]="form.get('color')?.value"
            (input)="form.get('color')?.setValue($any($event.target).value)">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Ícone (nome)</mat-label>
          <input matInput formControlName="icon" placeholder="utensils">
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button color="primary" (click)="submit()" [disabled]="loading">Salvar</button>
    </mat-dialog-actions>
  `
})
export class CategoryFormComponent implements OnInit {
  form = this.fb.group({
    name:  ['', Validators.required],
    type:  ['EXPENSE', Validators.required],
    color: ['#6366f1', Validators.required],
    icon:  ['tag', Validators.required]
  });
  loading = false;

  constructor(
    private fb: FormBuilder,
    private svc: CategoryService,
    private snack: MatSnackBar,
    public dialogRef: MatDialogRef<CategoryFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data?: Category
  ) {}

  ngOnInit() { if (this.data) this.form.patchValue(this.data); }

  submit() {
    if (this.form.invalid) return;
    this.loading = true;
    const val = this.form.value as any;
    const obs = this.data ? this.svc.update(this.data.id, val) : this.svc.create(val);
    obs.subscribe({
      next: () => { this.snack.open('Salvo!', 'OK', { duration: 2000, panelClass: 'success-snack' }); this.dialogRef.close(true); },
      error: err => { this.snack.open(err.error?.errors?.[0] ?? 'Erro', 'OK', { duration: 3000, panelClass: 'error-snack' }); this.loading = false; }
    });
  }
}
