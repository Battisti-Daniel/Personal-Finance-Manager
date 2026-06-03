import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Category } from '../models';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private base = '/api/v1/categories';

  constructor(private http: HttpClient) {}

  list() { return this.http.get<Category[]>(this.base); }

  create(data: { name: string; type: string; color: string; icon: string }) {
    return this.http.post<Category>(this.base, data);
  }

  update(id: string, data: Partial<{ name: string; type: string; color: string; icon: string }>) {
    return this.http.put<Category>(`${this.base}/${id}`, data);
  }

  delete(id: string) { return this.http.delete<void>(`${this.base}/${id}`); }
}
