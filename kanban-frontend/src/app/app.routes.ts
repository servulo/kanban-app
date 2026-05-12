import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'projects',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'projects',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/projects/projects.component').then(m => m.ProjectsComponent)
  },
  {
    path: 'projects/:id/board',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/board/board.component').then(m => m.BoardComponent)
  },
  {
    path: '**',
    redirectTo: 'projects'
  }
];
