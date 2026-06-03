export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: UserResponse;
}

export interface UserResponse {
  id: string;
  email: string;
  name: string;
}

export interface Category {
  id: string;
  name: string;
  type: 'INCOME' | 'EXPENSE';
  color: string;
  icon: string;
}

export interface Transaction {
  id: string;
  categoryId: string;
  categoryName: string;
  description: string;
  amount: number;
  type: 'INCOME' | 'EXPENSE';
  date: string;
  notes?: string;
  createdAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface Budget {
  id: string;
  categoryId: string;
  categoryName: string;
  yearMonth: string;
  amount: number;
}

export interface SummaryResponse {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  month: string;
}

export interface CategorySummary {
  categoryId: string;
  categoryName: string;
  total: number;
  percentage: number;
  budgetAmount?: number;
  budgetUsedPercentage?: number;
}

export interface CsvImportResponse {
  imported: number;
  autoCategorized: number;
  rejected: number;
  errors: { line: number; message: string }[];
}
