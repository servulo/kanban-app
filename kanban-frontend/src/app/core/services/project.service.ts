import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Project, CreateProjectRequest, Member } from '../models/project.model';

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/projects`;

  list(): Observable<Project[]> {
    return this.http.get<Project[]>(this.base);
  }

  create(request: CreateProjectRequest): Observable<Project> {
    return this.http.post<Project>(this.base, request);
  }

  update(id: number, request: CreateProjectRequest): Observable<Project> {
    return this.http.put<Project>(`${this.base}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  addMember(projectId: number, keycloakId: string, role: string): Observable<Member> {
    return this.http.post<Member>(`${this.base}/${projectId}/members`, { keycloakId, role });
  }

  removeMember(projectId: number, keycloakId: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${projectId}/members/${keycloakId}`);
  }
}
