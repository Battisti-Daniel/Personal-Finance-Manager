import { Component } from '@angular/core';
import { NgIf, NgFor } from '@angular/common';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TransactionService } from '../../core/services/transaction.service';
import { RefreshService } from '../../core/services/refresh.service';

@Component({
  selector: 'app-csv-import',
  standalone: true,
  imports: [NgIf, NgFor, MatDialogModule, MatButtonModule, MatIconModule, MatSnackBarModule, MatProgressBarModule],
  template: `
    <h2 mat-dialog-title>Importar CSV</h2>
    <mat-dialog-content>
      <p class="hint">Formato: <code>date,description,amount,categoryId,notes</code></p>
      <p class="hint">O campo <code>categoryId</code> é opcional — sem ele o sistema categoriza pela descrição.</p>

      <div class="drop-zone" (click)="fileInput.click()" (dragover)="$event.preventDefault()" (drop)="onDrop($event)">
        <mat-icon>upload_file</mat-icon>
        <span class="drop-label">{{ file ? file.name : 'Clique ou arraste o arquivo CSV aqui' }}</span>
        <span class="drop-sub" *ngIf="!file">Apenas arquivos .csv</span>
      </div>
      <input #fileInput type="file" accept=".csv" style="display:none" (change)="onFile($event)">

      <mat-progress-bar *ngIf="loading" mode="indeterminate" style="margin-top:16px;border-radius:4px"></mat-progress-bar>

      <div *ngIf="result" class="result-box" [class.has-errors]="result.rejected > 0">
        <div class="result-row" *ngIf="result.imported > 0">
          <mat-icon class="ok">check_circle</mat-icon>
          <span><strong>{{ result.imported }}</strong> transações importadas</span>
        </div>
        <div class="result-row" *ngIf="result.autoCategorized > 0">
          <mat-icon class="info">auto_awesome</mat-icon>
          <span><strong>{{ result.autoCategorized }}</strong> categorizadas automaticamente</span>
        </div>
        <div class="result-row" *ngIf="result.rejected > 0">
          <mat-icon class="err">cancel</mat-icon>
          <span><strong>{{ result.rejected }}</strong> rejeitadas</span>
        </div>
        <ul class="error-list" *ngIf="result.errors?.length">
          <li *ngFor="let e of result.errors">Linha {{ e.line }}: {{ e.message }}</li>
        </ul>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="dialogRef.close(false)" [disabled]="loading || uploaded">Cancelar</button>
      <button mat-raised-button color="primary" [disabled]="!file || loading || uploaded" (click)="upload()">
        <mat-icon>upload</mat-icon>
        {{ uploaded ? 'Importando...' : (loading ? 'Importando...' : 'Importar') }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    h2[mat-dialog-title] { min-width: min(480px, 90vw); }
    .hint { font-size: 12px; color: #64748b; margin-bottom: 4px;
      code { background: #f1f5f9; padding: 1px 5px; border-radius: 4px; font-size: 11px; }
    }
    .drop-zone {
      margin-top: 12px;
      border: 2px dashed #cbd5e1;
      border-radius: 10px;
      padding: 28px 20px;
      text-align: center;
      cursor: pointer;
      display: flex; flex-direction: column; align-items: center; gap: 6px;
      color: #64748b;
      transition: border-color .2s, background .2s;
      &:hover { border-color: #3b5bdb; background: #f5f8ff; color: #3b5bdb; }
      mat-icon { font-size: 32px; width: 32px; height: 32px; }
      .drop-label { font-size: 13px; font-weight: 500; }
      .drop-sub   { font-size: 11px; color: #94a3b8; }
    }
    .result-box {
      margin-top: 14px;
      border: 1px solid #d1fae5;
      background: #f0fdf4;
      border-radius: 8px;
      padding: 14px 16px;
      &.has-errors { border-color: #fee2e2; background: #fef2f2; }
    }
    .result-row {
      display: flex; align-items: center; gap: 8px;
      font-size: 13px; margin-bottom: 4px;
      mat-icon { font-size: 16px; width: 16px; height: 16px; }
      .ok   { color: #2f9e44; }
      .info { color: #3b5bdb; }
      .err  { color: #c92a2a; }
    }
    .error-list {
      margin-top: 8px; padding-left: 18px;
      font-size: 11px; color: #c92a2a;
      li { margin-bottom: 2px; }
    }
  `]
})
export class CsvImportComponent {
   file: File | null = null;
   loading = false;
   result: any = null;
   uploaded = false;

   constructor(
     private svc: TransactionService,
     private refresh: RefreshService,
     public dialogRef: MatDialogRef<CsvImportComponent>
   ) {}

   onFile(e: Event) {
     this.file = (e.target as HTMLInputElement).files?.[0] ?? null;
     this.result = null;
     this.uploaded = false;
   }

   onDrop(e: DragEvent) {
     e.preventDefault();
     this.file = e.dataTransfer?.files[0] ?? null;
     this.result = null;
     this.uploaded = false;
   }

    upload() {
      if (!this.file || this.loading || this.uploaded) return;
      this.loading = true;
      this.uploaded = true;
      this.svc.importCsv(this.file).subscribe({
        next: res => {
          this.result = res;
          this.loading = false;
          if (res.imported > 0) {
            this.refresh.refreshAll();
            setTimeout(() => this.dialogRef.close(true), 1500);
          } else {
            // nenhuma importada (só erros): libera o botão para nova tentativa
            this.uploaded = false;
          }
        },
        error: err => {
          this.result = err.error;
          this.loading = false;
          this.uploaded = false;
        }
      });
    }
}
