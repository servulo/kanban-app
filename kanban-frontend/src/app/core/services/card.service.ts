import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Card, CreateCardRequest } from '../models/board.model';

@Injectable({ providedIn: 'root' })
export class CardService {
  private http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/cards`;

  listByColumn(columnId: number): Observable<Card[]> {
    return this.http.get<Card[]>(`${this.base}/column/${columnId}`);
  }

  create(request: CreateCardRequest): Observable<Card> {
    return this.http.post<Card>(this.base, request);
  }

  update(id: number, request: Partial<CreateCardRequest>): Observable<Card> {
    return this.http.put<Card>(`${this.base}/${id}`, request);
  }

  move(id: number, columnId: number, position: number): Observable<Card> {
    return this.http.patch<Card>(`${this.base}/${id}/move`, { columnId, position });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
