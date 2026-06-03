import { Component, OnInit, OnDestroy } from '@angular/core';
import { DecimalPipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router, NavigationEnd } from '@angular/router';
import { BaseChartDirective } from 'ng2-charts';
import { ChartData, ChartOptions } from 'chart.js';
import { Chart, ArcElement, Tooltip, Legend, DoughnutController } from 'chart.js';
import { ReportService } from '../../core/services/report.service';
import { RefreshService } from '../../core/services/refresh.service';
import { SummaryResponse, CategorySummary } from '../../core/models';
import { forkJoin, Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

Chart.register(ArcElement, Tooltip, Legend, DoughnutController);

interface CategoryWithColor extends CategorySummary { color: string; }

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    NgIf, NgFor, DecimalPipe, FormsModule,
    MatIconModule, MatButtonModule, MatProgressSpinnerModule, MatTooltipModule,
    BaseChartDirective
  ],
  template: `
    <div class="page-header">
      <h1>Dashboard</h1>
      <div class="flex-gap">
        <input type="month" [(ngModel)]="month" (change)="load()" class="month-input">
        <button mat-icon-button (click)="load()" matTooltip="Atualizar" [disabled]="loading">
          <mat-icon>refresh</mat-icon>
        </button>
      </div>
    </div>

    <div *ngIf="loading" class="center-spinner">
      <mat-spinner diameter="40"></mat-spinner>
    </div>

    <ng-container *ngIf="!loading">
      <div class="summary-grid">
        <div class="summary-card income">
          <div class="card-left">
            <span class="label">Receitas</span>
            <span class="value">R$ {{ (summary?.totalIncome ?? 0) | number:'1.2-2' }}</span>
            <span class="sub">no mês selecionado</span>
          </div>
          <div class="card-icon"><mat-icon>trending_up</mat-icon></div>
        </div>
        <div class="summary-card expense">
          <div class="card-left">
            <span class="label">Despesas</span>
            <span class="value">R$ {{ (summary?.totalExpense ?? 0) | number:'1.2-2' }}</span>
            <span class="sub">no mês selecionado</span>
          </div>
          <div class="card-icon"><mat-icon>trending_down</mat-icon></div>
        </div>
        <div class="summary-card balance">
          <div class="card-left">
            <span class="label">Saldo</span>
            <span class="value">R$ {{ (summary?.balance ?? 0) | number:'1.2-2' }}</span>
            <span class="sub">líquido do mês</span>
          </div>
          <div class="card-icon"><mat-icon>account_balance</mat-icon></div>
        </div>
      </div>

      <div class="bottom-grid">
        <div class="pfm-card chart-card">
          <h3>Distribuição de despesas</h3>
          <ng-container *ngIf="categories.length > 0; else noData">
            <div class="chart-wrap">
              <canvas baseChart [data]="expenseData" type="doughnut" [options]="chartOptions"></canvas>
            </div>
          </ng-container>
          <ng-template #noData>
            <div class="empty-state">
              <mat-icon>pie_chart_outline</mat-icon>
              <p>Sem despesas no período</p>
            </div>
          </ng-template>
        </div>

        <div class="pfm-card breakdown-card">
          <h3>Detalhamento por categoria</h3>
          <div *ngIf="categories.length === 0" class="empty-state">
            <mat-icon>category</mat-icon>
            <p>Sem dados no período</p>
          </div>
          <div class="cat-list">
            <div class="cat-row" *ngFor="let c of categories">
              <div class="cat-dot" [style.background]="c.color"></div>
              <span class="cat-name">{{ c.categoryName }}</span>
              <div class="cat-bar-wrap">
                <div class="cat-bar" [style.width.%]="c.percentage" [style.background]="c.color"></div>
              </div>
              <span class="cat-pct">{{ c.percentage | number:'1.0-0' }}%</span>
              <span class="cat-val">R$ {{ c.total | number:'1.2-2' }}</span>
            </div>
          </div>
        </div>
      </div>
    </ng-container>
  `,
   styles: [`
     .bottom-grid { display: grid; grid-template-columns: 300px 1fr; gap: 16px; }
     h3 { font-size: 14px; font-weight: 600; color: #1e293b; margin-bottom: 20px; }
     .chart-wrap { max-width: 220px; margin: 0 auto; }

     .empty-state {
       display: flex; flex-direction: column; align-items: center; justify-content: center;
       padding: 40px 0; color: #94a3b8;
       mat-icon { font-size: 36px; width: 36px; height: 36px; margin-bottom: 8px; }
       p { font-size: 13px; }
     }

     .cat-list { display: flex; flex-direction: column; gap: 10px; }
     .cat-row {
       display: grid;
       grid-template-columns: 10px 1fr 120px 40px auto;
       align-items: center;
       gap: 10px;
     }
     .cat-dot  { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
     .cat-name { font-size: 13px; color: #334155; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
     .cat-bar-wrap { background: #f1f5f9; border-radius: 4px; height: 5px; overflow: hidden; }
     .cat-bar  { height: 100%; border-radius: 4px; transition: width .4s; }
     .cat-pct  { font-size: 12px; color: #94a3b8; text-align: right; }
     .cat-val  { font-size: 13px; font-weight: 600; color: #1e293b; text-align: right; min-width: 90px; }

     @media (max-width: 768px) {
       .bottom-grid { grid-template-columns: 1fr; }
       .cat-row { grid-template-columns: 10px 1fr auto; }
       .cat-bar-wrap, .cat-pct { display: none; }
     }

     @media (max-width: 480px) {
       .chart-wrap { max-width: 150px; }
       .cat-name { font-size: 12px; }
       .cat-val { font-size: 12px; }
     }
   `]
})
export class DashboardComponent implements OnInit, OnDestroy {
   month = new Date().toISOString().slice(0, 7);
   summary?: SummaryResponse;
   categories: CategoryWithColor[] = [];
   loading = false;
   private routerSub?: Subscription;
   private refreshSub?: Subscription;

   expenseData: ChartData<'doughnut'> = { labels: [], datasets: [{ data: [], backgroundColor: [] }] };
   chartOptions: ChartOptions<'doughnut'> = {
     responsive: true,
     cutout: '68%',
     plugins: { legend: { position: 'bottom', labels: { boxWidth: 12, font: { size: 12 } } } }
   };

   private palette = ['#3b5bdb','#2f9e44','#f59f00','#c92a2a','#7048e8','#0c8599','#d6336c','#5c7cfa'];

   constructor(private report: ReportService, private router: Router, private refresh: RefreshService) {}

   ngOnInit() {
     this.load();
     // recarrega sempre que navegar de volta para /dashboard
     this.routerSub = this.router.events.pipe(
       filter(e => e instanceof NavigationEnd && (e as NavigationEnd).url === '/dashboard')
     ).subscribe(() => this.load());
     // recarrega quando há importação de CSV
     this.refreshSub = this.refresh.onDashboardRefresh().subscribe(() => this.load());
   }

   ngOnDestroy() {
     this.routerSub?.unsubscribe();
     this.refreshSub?.unsubscribe();
   }

  load() {
    this.loading = true;
    forkJoin([
      this.report.summary(this.month),
      this.report.byCategory(this.month, 'EXPENSE')
    ]).subscribe({
      next: ([summary, cats]) => {
        this.summary = summary;
        this.categories = cats.map((c, i) => ({ ...c, color: this.palette[i % this.palette.length] }));
        this.expenseData = {
          labels: cats.map(c => c.categoryName),
          datasets: [{
            data: cats.map(c => c.total),
            backgroundColor: this.palette.slice(0, cats.length),
            borderWidth: 2,
            borderColor: '#fff'
          }]
        };
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}
