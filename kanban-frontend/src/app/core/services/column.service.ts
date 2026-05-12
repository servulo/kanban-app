import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Column, CreateColumnRequest } from '../models/board.model';

@Injectable({ providedIn: 'root' })
export class ColumnService {
  private http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/projects`;

  list(projectId: number): Observable<Column[]> {
    return this.http.get<Column[]>(`${this.base}/${projectId}/columns`);
  }

  create(projectId: number, request: CreateColumnRequest): Observable<Column> {
    return this.http.post<Column>(`${this.base}/${projectId}/columns`, request);
  }

  update(projectId: number, columnId: number, request: Partial<CreateColumnRequest>): Observable<Column> {
    return this.http.put<Column>(`${this.base}/${projectId}/columns/${columnId}`, request);
  }

  delete(projectId: number, columnId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${projectId}/columns/${columnId}`);
  }
}
