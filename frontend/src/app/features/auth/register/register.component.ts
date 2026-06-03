import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NgIf } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule, RouterLink, NgIf,
    MatFormFieldModule, MatInputModule, MatButtonModule,
    MatIconModule, MatSnackBarModule, MatProgressSpinnerModule
  ],
  template: `
    <div class="auth-page">
      <div class="auth-left">
        <div class="auth-brand">
          <div class="brand-icon"><mat-icon>account_balance_wallet</mat-icon></div>
          <h1>Comece a controlar suas finanças hoje</h1>
          <ul class="feature-list">
            <li><mat-icon>check_circle</mat-icon> Registro de receitas e despesas</li>
            <li><mat-icon>check_circle</mat-icon> Orçamentos mensais por categoria</li>
            <li><mat-icon>check_circle</mat-icon> Relatórios e gráficos detalhados</li>
            <li><mat-icon>check_circle</mat-icon> Importação de extratos CSV</li>
          </ul>
        </div>
      </div>

      <div class="auth-right">
        <div class="auth-form-wrap">
          <h2>Criar conta</h2>
          <p class="auth-sub">Gratuito e sem cartão de crédito</p>

          <form [formGroup]="form" (ngSubmit)="submit()" class="form-body">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Nome completo</mat-label>
              <mat-icon matPrefix>person_outline</mat-icon>
              <input matInput formControlName="name" autocomplete="name">
              <mat-error>Mínimo de 3 caracteres</mat-error>
            </mat-form-field>

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
              <input matInput [type]="showPwd ? 'text' : 'password'" formControlName="password" autocomplete="new-password">
              <button mat-icon-button matSuffix type="button" (click)="showPwd = !showPwd">
                <mat-icon>{{ showPwd ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              <mat-error>Mínimo de 3 caracteres</mat-error>
            </mat-form-field>

            <button mat-raised-button color="primary" type="submit" class="submit-btn" [disabled]="loading">
              <mat-spinner *ngIf="loading" diameter="18" class="inline-spinner"></mat-spinner>
              {{ loading ? 'Criando conta...' : 'Criar conta gratuita' }}
            </button>
          </form>

          <p class="switch-link">Já tem conta? <a routerLink="/auth/login">Entrar</a></p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-page { display: flex; min-height: 100vh; }

    .auth-left {
      flex: 1;
      background: linear-gradient(145deg, #1e3a8a 0%, #3b5bdb 100%);
      display: flex; align-items: center; justify-content: center;
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
    .auth-brand h1 { font-size: 28px; font-weight: 700; line-height: 1.3; margin-bottom: 24px; }
    .feature-list { list-style: none; display: flex; flex-direction: column; gap: 12px;
      li { display: flex; align-items: center; gap: 10px; font-size: 14px; color: rgba(255,255,255,.85);
        mat-icon { font-size: 18px; width: 18px; height: 18px; color: #93c5fd; }
      }
    }

    .auth-right {
      width: 480px;
      display: flex; align-items: center; justify-content: center;
      background: #fff;
      padding: 48px;
    }
    .auth-form-wrap { width: 100%; max-width: 360px; }
    h2 { font-size: 22px; font-weight: 700; color: #1e293b; margin-bottom: 4px; }
    .auth-sub { font-size: 14px; color: #64748b; margin-bottom: 28px; }
    .form-body { display: flex; flex-direction: column; gap: 4px; }
    .submit-btn { width: 100%; height: 46px; margin-top: 8px; border-radius: 8px !important; font-size: 14px !important; font-weight: 600 !important; }
    .inline-spinner { display: inline-block; margin-right: 8px; vertical-align: middle; }
    .switch-link { text-align: center; margin-top: 20px; color: #64748b; font-size: 13px;
      a { color: #3b5bdb; text-decoration: none; font-weight: 600; }
    }
    @media (max-width: 768px) { .auth-left { display: none; } .auth-right { width: 100%; } }
  `]
})
export class RegisterComponent {
  form = this.fb.group({
    name:     ['', [Validators.required, Validators.minLength(3)]],
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(3)]]
  });
  loading = false;
  showPwd = false;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private snack: MatSnackBar) {}

  submit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.auth.register({ ...this.form.value as any, deviceId: 'web' }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: err => {
        this.snack.open(err.error?.errors?.[0] ?? 'Erro ao criar conta', 'Fechar', { panelClass: 'error-snack', duration: 4000 });
        this.loading = false;
      }
    });
  }
}
