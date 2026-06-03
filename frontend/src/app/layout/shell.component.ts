import { Component, ViewChild } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { NgFor } from '@angular/common';
import { AuthService } from '../core/services/auth.service';

interface NavItem { label: string; icon: string; route: string; }

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [
    RouterOutlet, RouterLink, RouterLinkActive, NgFor,
    MatSidenavModule, MatListModule, MatIconModule,
    MatButtonModule, MatTooltipModule, MatDividerModule
  ],
  template: `
    <mat-sidenav-container class="shell-container">
      <mat-sidenav #sidenav mode="side" [opened]="isDesktop" class="sidenav" [fixedInViewport]="!isDesktop" [fixedTopGap]="!isDesktop ? 56 : 0">

        <!-- Brand -->
        <div class="brand">
          <div class="brand-logo">
            <mat-icon>account_balance_wallet</mat-icon>
          </div>
          <div class="brand-text">
            <span class="brand-name">PFM</span>
            <span class="brand-sub">Finance Manager</span>
          </div>
        </div>

        <div class="nav-section-label">PRINCIPAL</div>

        <nav class="nav-list">
          <a class="nav-item" *ngFor="let item of nav"
             [routerLink]="item.route"
             routerLinkActive="active"
             (click)="closeOnMobile()">
            <mat-icon class="nav-icon">{{ item.icon }}</mat-icon>
            <span class="nav-label">{{ item.label }}</span>
          </a>
        </nav>

        <div class="sidenav-spacer"></div>
        <mat-divider></mat-divider>

        <!-- User -->
        <div class="user-row">
          <div class="user-avatar">{{ initial }}</div>
          <div class="user-info">
            <span class="user-name">{{ user?.name }}</span>
            <span class="user-email">{{ user?.email }}</span>
          </div>
          <button mat-icon-button class="logout-btn" (click)="logout()" matTooltip="Sair">
            <mat-icon>logout</mat-icon>
          </button>
        </div>

      </mat-sidenav>

      <mat-sidenav-content class="main-content">
        <!-- Top bar mobile -->
        <div class="mobile-topbar" *ngIf="!isDesktop">
          <button mat-icon-button (click)="sidenav.toggle()">
            <mat-icon>menu</mat-icon>
          </button>
          <span class="topbar-title">PFM</span>
          <div style="width: 40px;"></div>
        </div>
        <router-outlet />
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: [`
    .shell-container { height: 100vh; }

    /* ── Sidebar ── */
    .sidenav {
      width: 230px;
      background: #0f172a;
      color: #94a3b8;
      display: flex;
      flex-direction: column;
      border-right: none !important;
    }

    /* Brand */
    .brand {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 20px 16px 20px;
      border-bottom: 1px solid rgba(255,255,255,.06);
    }
    .brand-logo {
      width: 36px; height: 36px;
      border-radius: 9px;
      background: #3b5bdb;
      display: flex; align-items: center; justify-content: center;
      mat-icon { color: #fff; font-size: 20px; width: 20px; height: 20px; }
    }
    .brand-name { display: block; font-size: 15px; font-weight: 700; color: #f1f5f9; letter-spacing: .3px; }
    .brand-sub  { display: block; font-size: 10px; color: #64748b; letter-spacing: .5px; text-transform: uppercase; margin-top: 1px; }

    /* Nav */
    .nav-section-label {
      font-size: 10px;
      font-weight: 700;
      letter-spacing: 1px;
      color: #334155;
      padding: 20px 18px 8px;
    }
    .nav-list { display: flex; flex-direction: column; gap: 2px; padding: 0 8px; }
    .nav-item {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 9px 10px;
      border-radius: 8px;
      color: #94a3b8;
      text-decoration: none;
      font-size: 13px;
      font-weight: 500;
      transition: background .15s, color .15s;
      cursor: pointer;

      .nav-icon { font-size: 18px; width: 18px; height: 18px; flex-shrink: 0; }

      &:hover { background: rgba(255,255,255,.05); color: #e2e8f0; }
      &.active { background: rgba(59,91,219,.2); color: #7c9ef7; }
    }

    .sidenav-spacer { flex: 1; }

    mat-divider { border-color: rgba(255,255,255,.06) !important; }

    /* User row */
    .user-row {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 14px 12px;
    }
    .user-avatar {
      width: 32px; height: 32px;
      border-radius: 50%;
      background: #3b5bdb;
      color: #fff;
      font-size: 13px;
      font-weight: 700;
      display: flex; align-items: center; justify-content: center;
      flex-shrink: 0;
    }
    .user-info { flex: 1; min-width: 0; }
    .user-name  { display: block; font-size: 13px; font-weight: 600; color: #e2e8f0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .user-email { display: block; font-size: 11px; color: #64748b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .logout-btn { color: #64748b !important; width: 32px; height: 32px; line-height: 32px;
      mat-icon { font-size: 18px; } }

    /* ── Content ── */
    .main-content { padding: 28px 32px; background: #f1f3f9; overflow-y: auto; }

    /* ── Mobile TopBar ── */
    .mobile-topbar {
      display: none;
      height: 56px;
      background: #0f172a;
      color: #fff;
      align-items: center;
      justify-content: space-between;
      padding: 0 16px;
      box-shadow: 0 2px 4px rgba(0,0,0,.1);
    }
    .topbar-title {
      font-size: 18px;
      font-weight: 700;
      color: #3b5bdb;
    }

    /* ── Responsive ── */
    @media (max-width: 768px) {
      .sidenav {
        width: 260px;
      }
      .main-content {
        padding: 16px;
      }
      .mobile-topbar {
        display: flex;
      }
      .brand {
        padding: 16px 12px;
      }
      .user-row {
        padding: 12px 8px;
      }
    }

    @media (max-width: 480px) {
      .sidenav {
        width: 100%;
        position: fixed;
        top: 56px;
        bottom: 0;
        left: 0;
        z-index: 1000;
      }
      .main-content {
        padding: 12px;
      }
      .brand-text {
        display: none;
      }
      .brand {
        justify-content: center;
        padding: 12px;
      }
      .nav-section-label {
        display: none;
      }
    }
  `]
})
export class ShellComponent {
  @ViewChild('sidenav') sidenav?: MatSidenav;
  
  nav: NavItem[] = [
    { label: 'Dashboard',   icon: 'dashboard',        route: '/dashboard' },
    { label: 'Transações',  icon: 'receipt_long',     route: '/transactions' },
    { label: 'Categorias',  icon: 'label',            route: '/categories' },
    { label: 'Orçamentos',  icon: 'savings',          route: '/budgets' },
    { label: 'Relatórios',  icon: 'bar_chart',        route: '/reports' }
  ];

  user = this.auth.getUser();
  get initial() { return this.user?.name?.charAt(0)?.toUpperCase() ?? 'U'; }
  get isDesktop() { return window.innerWidth > 768; }

  constructor(private auth: AuthService, private router: Router) {
    window.addEventListener('resize', () => this.onWindowResize());
  }

  onWindowResize() {
    if (this.isDesktop && this.sidenav) {
      this.sidenav.open();
    }
  }

  closeOnMobile() {
    if (!this.isDesktop && this.sidenav) {
      this.sidenav.close();
    }
  }

  logout() { this.auth.logout(); }
}
