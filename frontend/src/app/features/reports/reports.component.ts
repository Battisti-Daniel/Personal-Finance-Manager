import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { BaseChartDirective } from 'ng2-charts';
import { ChartData, ChartOptions } from 'chart.js';
import { Chart, BarElement, CategoryScale, LinearScale, BarController, Tooltip, Legend } from 'chart.js';
import { ReportService } from '../../core/services/report.service';
import { CategorySummary } from '../../core/models';
import { forkJoin } from 'rxjs';

Chart.register(BarElement, CategoryScale, LinearScale, BarController, Tooltip, Legend);

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [
    NgFor, NgIf, DecimalPipe, FormsModule,
    MatButtonToggleModule, MatProgressSpinnerModule, MatTableModule, MatIconModule,
    BaseChartDirective
  ],
  template: `
    <div class="page-header">
      <h1>Relatórios</h1>
      <input type="month" [(ngModel)]="month" (change)="load()" class="month-input">
    </div>

    <div *ngIf="loading" class="center-spinner"><mat-spinner diameter="40"></mat-spinner></div>

    <ng-container *ngIf="!loading">
      <div class="charts-grid">
        <div class="pfm-card">
          <h3>Despesas por categoria</h3>
          <div *ngIf="expenseData.datasets[0].data.length > 0; else noExpense">
            <canvas baseChart [data]="expenseData" type="bar" [options]="barOptions"></canvas>
          </div>
          <ng-template #noExpense><p class="no-data">Sem despesas</p></ng-template>
        </div>

        <div class="pfm-card">
          <h3>Receitas por categoria</h3>
          <div *ngIf="incomeData.datasets[0].data.length > 0; else noIncome">
            <canvas baseChart [data]="incomeData" type="bar" [options]="barOptions"></canvas>
          </div>
          <ng-template #noIncome><p class="no-data">Sem receitas</p></ng-template>
        </div>
      </div>

      <div class="pfm-card mt-16">
        <h3>Detalhamento de Despesas</h3>
        <table mat-table [dataSource]="expenses">
          <ng-container matColumnDef="category">
            <th mat-header-cell *matHeaderCellDef>Categoria</th>
            <td mat-cell *matCellDef="let c">{{ c.categoryName }}</td>
          </ng-container>
          <ng-container matColumnDef="total">
            <th mat-header-cell *matHeaderCellDef>Total</th>
            <td mat-cell *matCellDef="let c">R$ {{ c.total | number:'1.2-2' }}</td>
          </ng-container>
          <ng-container matColumnDef="pct">
            <th mat-header-cell *matHeaderCellDef>% do total</th>
            <td mat-cell *matCellDef="let c">{{ c.percentage | number:'1.1-1' }}%</td>
          </ng-container>
          <ng-container matColumnDef="budget">
            <th mat-header-cell *matHeaderCellDef>Orçamento</th>
            <td mat-cell *matCellDef="let c">
              <span *ngIf="c.budgetAmount">
                R$ {{ c.budgetAmount | number:'1.2-2' }}
                <span [class]="(c.budgetUsedPercentage ?? 0) >= 100 ? 'over-budget' : 'ok-budget'">
                  ({{ c.budgetUsedPercentage | number:'1.0-0' }}%)
                </span>
              </span>
              <span *ngIf="!c.budgetAmount" class="no-budget">Sem orçamento</span>
            </td>
          </ng-container>
          <tr mat-header-row *matHeaderRowDef="cols"></tr>
          <tr mat-row *matRowDef="let row; columns: cols;"></tr>
        </table>
      </div>
    </ng-container>
  `,
  styles: [`
    .month-input { border: 1px solid #ddd; border-radius: 8px; padding: 8px 12px; font-size: 14px; outline: none; }
    .center-spinner { display: flex; justify-content: center; padding: 48px; }
    .charts-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 0; }
    h3 { font-size: 16px; font-weight: 600; margin-bottom: 16px; }
    .no-data { color: #aaa; text-align: center; padding: 32px 0; }
    .over-budget { color: #ef4444; font-weight: 600; }
    .ok-budget { color: #10b981; font-weight: 600; }
    .no-budget { color: #aaa; font-size: 13px; }
  `]
})
export class ReportsComponent implements OnInit {
  month = new Date().toISOString().slice(0, 7);
  loading = false;
  expenses: CategorySummary[] = [];
  cols = ['category', 'total', 'pct', 'budget'];

  expenseData: ChartData<'bar'> = { labels: [], datasets: [{ data: [], label: 'Despesas', backgroundColor: '#6366f1' }] };
  incomeData:  ChartData<'bar'> = { labels: [], datasets: [{ data: [], label: 'Receitas',  backgroundColor: '#10b981' }] };
  barOptions: ChartOptions<'bar'> = { responsive: true, plugins: { legend: { display: false } } };

  constructor(private report: ReportService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    forkJoin([
      this.report.byCategory(this.month, 'EXPENSE'),
      this.report.byCategory(this.month, 'INCOME')
    ]).subscribe(([exp, inc]) => {
      this.expenses = exp;
      this.expenseData = { labels: exp.map(c => c.categoryName), datasets: [{ data: exp.map(c => c.total), label: 'Despesas', backgroundColor: '#6366f1' }] };
      this.incomeData  = { labels: inc.map(c => c.categoryName), datasets: [{ data: inc.map(c => c.total), label: 'Receitas',  backgroundColor: '#10b981' }] };
      this.loading = false;
    });
  }
}
