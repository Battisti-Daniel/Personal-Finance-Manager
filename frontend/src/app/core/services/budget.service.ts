import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Budget } from '../models';

@Injectable({ providedIn: 'root' })
export class BudgetService {
  private base = '/api/v1/budgets';

  constructor(private http: HttpClient) {}

  list(month: string) {
    return this.http.get<Budget[]>(this.base, { params: new HttpParams().set('month', month) });
  }

  create(data: { categoryId: string; yearMonth: string; amount: number }) {
    return this.http.post<Budget>(this.base, data);
  }

  update(id: string, amount: number) {
    return this.http.put<Budget>(`${this.base}/${id}`, { amount });
  }

  delete(id: string) { return this.http.delete<void>(`${this.base}/${id}`); }
}
