import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { BudgetService } from '../../core/services/budget.service';
import { CategoryService } from '../../core/services/category.service';
import { ReportService } from '../../core/services/report.service';
import { Budget, Category, CategorySummary } from '../../core/models';
import { BudgetFormComponent } from './budget-form.component';
import { forkJoin } from 'rxjs';

interface BudgetRow extends Budget { spent: number; pct: number; }

@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [
    NgFor, NgIf, DecimalPipe, FormsModule,
    MatTableModule, MatButtonModule, MatIconModule, MatFormFieldModule,
    MatInputModule, MatDialogModule, MatSnackBarModule,
    MatProgressSpinnerModule, MatProgressBarModule
  ],
  template: `
    <div class="page-header">
      <h1>Orçamentos</h1>
      <div class="flex-gap">
        <input type="month" [(ngModel)]="month" (change)="load()" class="month-input">
        <button mat-raised-button color="primary" (click)="openForm()">
          <mat-icon>add</mat-icon> Novo orçamento
        </button>
      </div>
    </div>

    <div *ngIf="loading" class="center-spinner"><mat-spinner diameter="40"></mat-spinner></div>

    <div class="pfm-card" *ngIf="!loading">
      <p *ngIf="rows.length === 0" class="no-data">Nenhum orçamento para este mês.</p>
      <table mat-table [dataSource]="rows" *ngIf="rows.length > 0">
        <ng-container matColumnDef="category">
          <th mat-header-cell *matHeaderCellDef>Categoria</th>
          <td mat-cell *matCellDef="let r">{{ r.categoryName }}</td>
        </ng-container>
        <ng-container matColumnDef="limit">
          <th mat-header-cell *matHeaderCellDef>Limite</th>
          <td mat-cell *matCellDef="let r">R$ {{ r.amount | number:'1.2-2' }}</td>
        </ng-container>
        <ng-container matColumnDef="spent">
          <th mat-header-cell *matHeaderCellDef>Gasto</th>
          <td mat-cell *matCellDef="let r">R$ {{ r.spent | number:'1.2-2' }}</td>
        </ng-container>
        <ng-container matColumnDef="progress">
          <th mat-header-cell *matHeaderCellDef>Progresso</th>
          <td mat-cell *matCellDef="let r" style="min-width:160px">
            <mat-progress-bar
              [value]="r.pct"
              [color]="r.pct >= 100 ? 'warn' : r.pct >= 80 ? 'accent' : 'primary'"
              style="border-radius:4px">
            </mat-progress-bar>
            <span class="pct-label">{{ r.pct | number:'1.0-0' }}%</span>
          </td>
        </ng-container>
        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef></th>
          <td mat-cell *matCellDef="let r">
            <button mat-icon-button (click)="openForm(r)"><mat-icon>edit</mat-icon></button>
            <button mat-icon-button color="warn" (click)="delete(r)"><mat-icon>delete</mat-icon></button>
          </td>
        </ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols;"></tr>
      </table>
    </div>
  `,
  styles: [`
    .month-input { border: 1px solid #ddd; border-radius: 8px; padding: 8px 12px; font-size: 14px; outline: none; height: 40px; }
    .center-spinner { display: flex; justify-content: center; padding: 48px; }
    .no-data { color: #aaa; padding: 24px 0; text-align: center; }
    .pct-label { font-size: 12px; color: #888; margin-left: 8px; }
  `]
})
export class BudgetsComponent implements OnInit {
  cols = ['category', 'limit', 'spent', 'progress', 'actions'];
  month = new Date().toISOString().slice(0, 7);
  rows: BudgetRow[] = [];
  categories: Category[] = [];
  loading = false;

  constructor(
    private svc: BudgetService,
    private catSvc: CategoryService,
    private report: ReportService,
    private dialog: MatDialog,
    private snack: MatSnackBar
  ) {}

  ngOnInit() {
    this.catSvc.list().subscribe(c => this.categories = c);
    this.load();
  }

  load() {
    this.loading = true;
    forkJoin([
      this.svc.list(this.month),
      this.report.byCategory(this.month, 'EXPENSE')
    ]).subscribe(([budgets, cats]) => {
      this.rows = budgets.map(b => {
        const cat = cats.find(c => c.categoryId === b.categoryId);
        const spent = cat?.total ?? 0;
        const pct = b.amount > 0 ? Math.min((spent / b.amount) * 100, 100) : 0;
        return { ...b, spent, pct };
      });
      this.loading = false;
    });
  }

  openForm(b?: Budget) {
    const ref = this.dialog.open(BudgetFormComponent, {
      width: '440px',
      data: { budget: b, month: this.month, categories: this.categories }
    });
    ref.afterClosed().subscribe(ok => ok && this.load());
  }

  delete(b: Budget) {
    if (!confirm(`Excluir orçamento de "${b.categoryName}"?`)) return;
    this.svc.delete(b.id).subscribe({
      next: () => { this.snack.open('Excluído', 'OK', { duration: 2000, panelClass: 'success-snack' }); this.load(); },
      error: () => this.snack.open('Erro', 'OK', { duration: 3000, panelClass: 'error-snack' })
    });
  }
}
