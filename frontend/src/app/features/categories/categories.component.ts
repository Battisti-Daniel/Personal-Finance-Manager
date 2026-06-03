import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CategoryService } from '../../core/services/category.service';
import { Category } from '../../core/models';
import { CategoryFormComponent } from './category-form.component';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [
    NgFor, NgIf, FormsModule, ReactiveFormsModule,
    MatTableModule, MatButtonModule, MatIconModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatDialogModule, MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="page-header">
      <h1>Categorias</h1>
      <button mat-raised-button color="primary" (click)="openForm()">
        <mat-icon>add</mat-icon> Nova categoria
      </button>
    </div>

    <div *ngIf="loading" class="center-spinner"><mat-spinner diameter="40"></mat-spinner></div>

    <div class="pfm-card" *ngIf="!loading">
      <table mat-table [dataSource]="categories">
        <ng-container matColumnDef="color">
          <th mat-header-cell *matHeaderCellDef></th>
          <td mat-cell *matCellDef="let c">
            <div class="color-dot" [style.background]="c.color"></div>
          </td>
        </ng-container>
        <ng-container matColumnDef="name">
          <th mat-header-cell *matHeaderCellDef>Nome</th>
          <td mat-cell *matCellDef="let c">{{ c.name }}</td>
        </ng-container>
        <ng-container matColumnDef="type">
          <th mat-header-cell *matHeaderCellDef>Tipo</th>
          <td mat-cell *matCellDef="let c">
            <span [class]="c.type === 'INCOME' ? 'chip-income' : 'chip-expense'">
              {{ c.type === 'INCOME' ? 'Receita' : 'Despesa' }}
            </span>
          </td>
        </ng-container>
        <ng-container matColumnDef="icon">
          <th mat-header-cell *matHeaderCellDef>Ícone</th>
          <td mat-cell *matCellDef="let c">{{ c.icon }}</td>
        </ng-container>
        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef></th>
          <td mat-cell *matCellDef="let c">
            <button mat-icon-button (click)="openForm(c)"><mat-icon>edit</mat-icon></button>
            <button mat-icon-button color="warn" (click)="delete(c)"><mat-icon>delete</mat-icon></button>
          </td>
        </ng-container>

        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols;"></tr>
      </table>
    </div>
  `,
  styles: [`
    .center-spinner { display: flex; justify-content: center; padding: 48px; }
    .color-dot { width: 16px; height: 16px; border-radius: 50%; }
  `]
})
export class CategoriesComponent implements OnInit {
  cols = ['color', 'name', 'type', 'icon', 'actions'];
  categories: Category[] = [];
  loading = false;

  constructor(private svc: CategoryService, private dialog: MatDialog, private snack: MatSnackBar) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.svc.list().subscribe(c => { this.categories = c; this.loading = false; });
  }

  openForm(c?: Category) {
    const ref = this.dialog.open(CategoryFormComponent, { width: '440px', data: c });
    ref.afterClosed().subscribe(ok => ok && this.load());
  }

  delete(c: Category) {
    if (!confirm(`Excluir categoria "${c.name}"?`)) return;
    this.svc.delete(c.id).subscribe({
      next: () => { this.snack.open('Excluída', 'OK', { duration: 2000, panelClass: 'success-snack' }); this.load(); },
      error: () => this.snack.open('Erro ao excluir', 'OK', { duration: 3000, panelClass: 'error-snack' })
    });
  }
}
