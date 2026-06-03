import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { CategorySummary, SummaryResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private base = '/api/v1/reports';

  constructor(private http: HttpClient) {}

  summary(month: string) {
    return this.http.get<SummaryResponse>(`${this.base}/summary`, {
      params: new HttpParams().set('month', month)
    });
  }

  byCategory(month: string, type: 'INCOME' | 'EXPENSE') {
    return this.http.get<CategorySummary[]>(`${this.base}/by-category`, {
      params: new HttpParams().set('month', month).set('type', type)
    });
  }
}
