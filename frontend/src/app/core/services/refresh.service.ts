import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RefreshService {
  private transactionsRefresh$ = new Subject<void>();
  private dashboardRefresh$ = new Subject<void>();

  onTransactionsRefresh() {
    return this.transactionsRefresh$.asObservable();
  }

  onDashboardRefresh() {
    return this.dashboardRefresh$.asObservable();
  }

  refreshTransactions() {
    this.transactionsRefresh$.next();
  }

  refreshDashboard() {
    this.dashboardRefresh$.next();
  }

  refreshAll() {
    this.refreshTransactions();
    this.refreshDashboard();
  }
}

