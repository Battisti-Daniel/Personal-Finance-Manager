import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.authRoutes)
  },
  {
    path: '',
    loadComponent: () => import('./layout/shell.component').then(m => m.ShellComponent),
    canActivate: [authGuard],
    children: [
      { path: 'dashboard',     loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'transactions',  loadComponent: () => import('./features/transactions/transactions.component').then(m => m.TransactionsComponent) },
      { path: 'categories',    loadComponent: () => import('./features/categories/categories.component').then(m => m.CategoriesComponent) },
      { path: 'budgets',       loadComponent: () => import('./features/budgets/budgets.component').then(m => m.BudgetsComponent) },
      { path: 'reports',       loadComponent: () => import('./features/reports/reports.component').then(m => m.ReportsComponent) }
    ]
  },
  { path: '**', redirectTo: '/dashboard' }
];
