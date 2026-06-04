import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { NgFor, NgIf, DecimalPipe, DatePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatPaginatorModule, PageEvent, MatPaginator } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TransactionService } from '../../core/services/transaction.service';
import { CategoryService } from '../../core/services/category.service';
import { RefreshService } from '../../core/services/refresh.service';
import { Transaction, Category } from '../../core/models';
import { TransactionFormComponent } from './transaction-form.component';
import { CsvImportComponent } from './csv-import.component';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [
    NgFor, NgIf, DecimalPipe, DatePipe, FormsModule, ReactiveFormsModule,
    MatTableModule, MatButtonModule, MatIconModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatDialogModule, MatSnackBarModule,
    MatPaginatorModule, MatProgressSpinnerModule, MatChipsModule, MatTooltipModule
  ],
  template: `
    <div class="page-header">
      <h1>Transações</h1>
      <div class="flex-gap">
        <button mat-stroked-button (click)="openImport()">
          <mat-icon>upload_file</mat-icon> Importar CSV
        </button>
        <button mat-raised-button color="primary" (click)="openForm()">
          <mat-icon>add</mat-icon> Nova transação
        </button>
      </div>
    </div>

    <!-- Filtros -->
    <div class="pfm-card filters-card">
      <div class="filters-row">
        <input type="month" [(ngModel)]="filters.month" (change)="onFilterChange()" class="month-input" placeholder="Mês">
        <mat-form-field appearance="outline" class="filter-field">
          <mat-label>Tipo</mat-label>
          <mat-select [(ngModel)]="filters.type" (ngModelChange)="onFilterChange()">
            <mat-option value="">Todos</mat-option>
            <mat-option value="INCOME">Receita</mat-option>
            <mat-option value="EXPENSE">Despesa</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="filter-field">
          <mat-label>Categoria</mat-label>
          <mat-select [(ngModel)]="filters.categoryId" (ngModelChange)="onFilterChange()">
            <mat-option value="">Todas</mat-option>
            <mat-option *ngFor="let c of categories" [value]="c.id">{{ c.name }}</mat-option>
          </mat-select>
        </mat-form-field>
        <button mat-stroked-button (click)="clearFilters()">
          <mat-icon>clear</mat-icon> Limpar
        </button>
      </div>
    </div>

    <div *ngIf="loading" class="center-spinner"><mat-spinner diameter="40"></mat-spinner></div>

    <div class="pfm-card mt-16" *ngIf="!loading">
      <table mat-table [dataSource]="transactions">
        <ng-container matColumnDef="date">
          <th mat-header-cell *matHeaderCellDef>Data</th>
          <td mat-cell *matCellDef="let t">{{ t.date | date:'dd/MM/yyyy' }}</td>
        </ng-container>
        <ng-container matColumnDef="description">
          <th mat-header-cell *matHeaderCellDef>Descrição</th>
          <td mat-cell *matCellDef="let t">{{ t.description }}</td>
        </ng-container>
        <ng-container matColumnDef="category">
          <th mat-header-cell *matHeaderCellDef>Categoria</th>
          <td mat-cell *matCellDef="let t">{{ t.categoryName }}</td>
        </ng-container>
        <ng-container matColumnDef="type">
          <th mat-header-cell *matHeaderCellDef>Tipo</th>
          <td mat-cell *matCellDef="let t">
            <span [class]="t.type === 'INCOME' ? 'chip-income' : 'chip-expense'">
              {{ t.type === 'INCOME' ? 'Receita' : 'Despesa' }}
            </span>
          </td>
        </ng-container>
        <ng-container matColumnDef="amount">
          <th mat-header-cell *matHeaderCellDef>Valor</th>
          <td mat-cell *matCellDef="let t" [class]="t.type === 'INCOME' ? 'income-val' : 'expense-val'">
            {{ t.type === 'INCOME' ? '+' : '-' }} R$ {{ t.amount | number:'1.2-2' }}
          </td>
        </ng-container>
        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef></th>
          <td mat-cell *matCellDef="let t">
            <button mat-icon-button (click)="openForm(t)" matTooltip="Editar"><mat-icon>edit</mat-icon></button>
            <button mat-icon-button color="warn" (click)="delete(t)" matTooltip="Excluir"><mat-icon>delete</mat-icon></button>
          </td>
        </ng-container>

        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols;"></tr>
      </table>

      <mat-paginator
        [length]="total"
        [pageSize]="10"
        [pageIndex]="filters.page"
        [pageSizeOptions]="[10, 20, 50]"
        (page)="onPage($event)">
      </mat-paginator>
    </div>
  `,
   styles: [`
     .filters-card { margin-bottom: 16px; padding: 16px 24px; }
     .filters-row { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; }
     .month-input { border: 1px solid #ddd; border-radius: 8px; padding: 8px 12px; font-size: 14px; outline: none; height: 56px; }
     .filter-field { min-width: 140px; }
     .center-spinner { display: flex; justify-content: center; padding: 48px; }
     .income-val { color: #10b981; font-weight: 600; }
     .expense-val { color: #ef4444; font-weight: 600; }
     mat-paginator { border-top: 1px solid #f0f0f0; }

     @media (max-width: 768px) {
       .filters-card { padding: 12px 16px; }
       .filters-row { gap: 8px; }
       .filter-field { min-width: 120px; }
       .month-input { height: 40px; font-size: 12px; }

       /* esconde categoria e tipo na tabela */
       .mat-column-category,
       .mat-column-type { display: none; }
     }

     @media (max-width: 480px) {
       .filters-card { padding: 8px 12px; margin-bottom: 12px; }
       .filters-row { flex-direction: column; gap: 8px; }
       .filter-field { width: 100%; min-width: 100%; }
       .month-input { width: 100%; }

       /* no celular pequeno, data mais compacta */
       .mat-column-date { font-size: 12px; }
       .mat-column-description { font-size: 13px; }
       .mat-column-amount { font-size: 13px; }
       .mat-column-actions { width: 72px; }
       .mat-column-actions button { transform: scale(0.85); }
     }
   `]
})
export class TransactionsComponent implements OnInit, OnDestroy {
   @ViewChild(MatPaginator) paginator?: MatPaginator;

   cols = ['date', 'description', 'category', 'type', 'amount', 'actions'];
   transactions: Transaction[] = [];
   categories: Category[] = [];
   total = 0;
   loading = false;
   filters = { month: '', type: '', categoryId: '', page: 0, size: 10 };
   private refreshSub?: Subscription;

   constructor(
     private svc: TransactionService,
     private catSvc: CategoryService,
     private dialog: MatDialog,
     private snack: MatSnackBar,
     private refresh: RefreshService
   ) {}

   ngOnInit() {
     this.catSvc.list().subscribe(c => this.categories = c);
     this.load();
     // se importação foi bem-sucedida, recarrega a lista
     this.refreshSub = this.refresh.onTransactionsRefresh().subscribe(() => this.load());
   }

   ngOnDestroy() {
     this.refreshSub?.unsubscribe();
   }

   load() {
     this.loading = true;
     const f = { ...this.filters };
     if (!f.month) delete (f as any).month;
     if (!f.type) delete (f as any).type;
     if (!f.categoryId) delete (f as any).categoryId;
     console.log('Loading transactions with filters:', f);
     this.svc.list(f).subscribe(p => {
       this.transactions = p.content;
       this.total = p.totalElements;
       this.loading = false;
       console.log('Loaded page', f.page, 'with', p.content.length, 'items, total:', p.totalElements);
     });
   }

   clearFilters() {
     this.filters = { month: '', type: '', categoryId: '', page: 0, size: 10 };
     this.load();
   }

   onFilterChange() {
     this.filters.page = 0;
     if (this.paginator) {
       this.paginator.firstPage();
     }
     this.load();
   }

   onPage(e: PageEvent) {
     console.log('Page event:', e);
     this.filters.page = e.pageIndex;
     this.filters.size = e.pageSize;
     console.log('Updated filters to page:', this.filters.page, 'size:', this.filters.size);
     this.load();
   }

  openForm(t?: Transaction) {
    const ref = this.dialog.open(TransactionFormComponent, {
      width: '480px',
      data: { transaction: t, categories: this.categories }
    });
    ref.afterClosed().subscribe(ok => ok && this.load());
  }

   openImport() {
     const ref = this.dialog.open(CsvImportComponent, { width: '520px' });
     ref.afterClosed().subscribe(ok => {
       if (ok) {
         this.load();
         this.refresh.refreshDashboard(); // avisa o dashboard para recarregar
       }
     });
   }

   delete(t: Transaction) {
     if (!confirm(`Excluir "${t.description}"?`)) return;
     this.svc.delete(t.id).subscribe({
       next: () => { this.snack.open('Excluída', 'OK', { duration: 2000, panelClass: 'success-snack' }); this.load(); },
       error: () => this.snack.open('Erro ao excluir', 'OK', { duration: 3000, panelClass: 'error-snack' })
     });
   }
}
