import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Page, Transaction } from '../models';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private base = '/api/v1/transactions';

  constructor(private http: HttpClient) {}

  list(filters: { month?: string; categoryId?: string; type?: string; page?: number; size?: number }) {
    let params = new HttpParams();
    if (filters.month)      params = params.set('month', filters.month);
    if (filters.categoryId) params = params.set('categoryId', filters.categoryId);
    if (filters.type)       params = params.set('type', filters.type);
    params = params.set('page', filters.page ?? 0);
    params = params.set('size', filters.size ?? 10);
    params = params.set('sort', 'createdAt,desc');
    return this.http.get<Page<Transaction>>(this.base, { params });
  }

  create(data: { categoryId: string; description: string; amount: number; date: string; notes?: string }) {
    return this.http.post<Transaction>(this.base, data);
  }

  update(id: string, data: Partial<{ description: string; amount: number; date: string; notes: string }>) {
    return this.http.put<Transaction>(`${this.base}/${id}`, data);
  }

   delete(id: string) {
     return this.http.delete<void>(`${this.base}/${id}`);
   }


   importCsv(file: File) {
     const form = new FormData();
     form.append('file', file);
     return this.http.post<any>(`${this.base}/import`, form);
   }
}
