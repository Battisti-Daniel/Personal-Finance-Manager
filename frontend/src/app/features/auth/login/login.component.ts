import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NgIf } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule, RouterLink, NgIf,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatSnackBarModule, MatProgressSpinnerModule
  ],
  template: `
    <div class="auth-page">
      <div class="auth-left">
        <div class="auth-brand">
          <div class="brand-icon"><mat-icon>account_balance_wallet</mat-icon></div>
          <h1>Personal Finance<br>Manager</h1>
          <p>Gerencie suas finanças de forma simples, segura e inteligente.</p>
        </div>
      </div>

      <div class="auth-right">
        <div class="auth-form-wrap">
          <h2>Entrar na conta</h2>
          <p class="auth-sub">Bem-vindo de volta</p>

          <form [formGroup]="form" (ngSubmit)="submit()" class="form-body">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>E-mail</mat-label>
              <mat-icon matPrefix>mail_outline</mat-icon>
              <input matInput type="email" formControlName="email" autocomplete="email">
              <mat-error *ngIf="form.get('email')?.hasError('required')">E-mail obrigatório</mat-error>
              <mat-error *ngIf="form.get('email')?.hasError('email')">E-mail inválido</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Senha</mat-label>
              <mat-icon matPrefix>lock_outline</mat-icon>
              <input matInput [type]="showPwd ? 'text' : 'password'" formControlName="password" autocomplete="current-password">
              <button mat-icon-button matSuffix type="button" (click)="showPwd = !showPwd">
                <mat-icon>{{ showPwd ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              <mat-error>Senha obrigatória</mat-error>
            </mat-form-field>

            <button mat-raised-button color="primary" type="submit" class="submit-btn" [disabled]="loading">
              <mat-spinner *ngIf="loading" diameter="18" class="inline-spinner"></mat-spinner>
              {{ loading ? 'Entrando...' : 'Entrar' }}
            </button>
          </form>

          <p class="switch-link">Não tem conta? <a routerLink="/auth/register">Criar conta</a></p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-page { display: flex; min-height: 100vh; }

    .auth-left {
      flex: 1;
      background: linear-gradient(145deg, #1e3a8a 0%, #3b5bdb 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 48px;
    }
    .auth-brand { max-width: 360px; color: #fff; }
    .brand-icon {
      width: 56px; height: 56px;
      background: rgba(255,255,255,.15);
      border-radius: 14px;
      display: flex; align-items: center; justify-content: center;
      margin-bottom: 28px;
      mat-icon { font-size: 28px; width: 28px; height: 28px; color: #fff; }
    }
    .auth-brand h1 { font-size: 32px; font-weight: 700; line-height: 1.2; margin-bottom: 16px; }
    .auth-brand p  { font-size: 15px; color: rgba(255,255,255,.75); line-height: 1.6; }

    .auth-right {
      width: 480px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #fff;
      padding: 48px;
    }
    .auth-form-wrap { width: 100%; max-width: 360px; }
    h2 { font-size: 22px; font-weight: 700; color: #1e293b; margin-bottom: 4px; }
    .auth-sub { font-size: 14px; color: #64748b; margin-bottom: 28px; }

    .form-body { display: flex; flex-direction: column; gap: 4px; }
    .submit-btn {
      width: 100%;
      height: 46px;
      margin-top: 8px;
      border-radius: 8px !important;
      font-size: 14px !important;
      font-weight: 600 !important;
    }
    .inline-spinner { display: inline-block; margin-right: 8px; vertical-align: middle; }
    .switch-link { text-align: center; margin-top: 20px; color: #64748b; font-size: 13px;
      a { color: #3b5bdb; text-decoration: none; font-weight: 600; }
    }

    @media (max-width: 768px) {
      .auth-left { display: none; }
      .auth-right { width: 100%; }
    }
  `]
})
export class LoginComponent {
  form = this.fb.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });
  loading = false;
  showPwd = false;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private snack: MatSnackBar) {}

  submit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.auth.login({ ...this.form.value as any, deviceId: 'web' }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: err => {
        this.snack.open(err.error?.errors?.[0] ?? 'Credenciais inválidas', 'Fechar', { panelClass: 'error-snack', duration: 4000 });
        this.loading = false;
      }
    });
  }
}
