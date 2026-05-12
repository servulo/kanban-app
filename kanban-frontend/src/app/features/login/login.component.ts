import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  template: `
    <div class="min-h-screen grid lg:grid-cols-2">

      <!-- Painel esquerdo — branding -->
      <div class="hidden lg:flex flex-col justify-between bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 p-12 text-white">
        <div class="flex items-center gap-3">
          <div class="w-9 h-9 rounded-lg bg-indigo-500 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <span class="text-xl font-bold tracking-tight">Kanban</span>
        </div>

        <div>
          <p class="text-4xl font-bold leading-tight mb-4">
            Organize seu trabalho<br/>de forma visual e eficiente.
          </p>
          <p class="text-slate-400 text-lg">
            Projetos, colunas e cards — tudo em um só lugar.
          </p>
        </div>

        <p class="text-slate-500 text-sm">© 2026 SPRJ. Todos os direitos reservados.</p>
      </div>

      <!-- Painel direito — formulário -->
      <div class="flex items-center justify-center p-8 bg-slate-50">
        <div class="w-full max-w-sm">

          <!-- Logo mobile -->
          <div class="flex items-center gap-2 mb-10 lg:hidden">
            <div class="w-8 h-8 rounded-lg bg-indigo-600 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </div>
            <span class="text-lg font-bold text-slate-800">Kanban</span>
          </div>

          <h1 class="text-2xl font-bold text-slate-900 mb-1">Bem-vindo de volta</h1>
          <p class="text-slate-500 text-sm mb-8">Entre com suas credenciais para continuar.</p>

          @if (error()) {
            <div class="flex items-center gap-3 bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 mb-6 text-sm">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>{{ error() }}</span>
            </div>
          }

          <form (ngSubmit)="onSubmit()" class="flex flex-col gap-5">

            <!-- Usuário -->
            <div class="flex flex-col gap-1.5">
              <label class="text-sm font-medium text-slate-700">Usuário</label>
              <input
                type="text"
                name="username"
                [(ngModel)]="username"
                placeholder="seu.usuario"
                autocomplete="username"
                class="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 bg-white text-sm text-slate-900
                       placeholder-slate-400 outline-none transition
                       focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20"
              />
            </div>

            <!-- Senha -->
            <div class="flex flex-col gap-1.5">
              <div class="flex items-center justify-between">
                <label class="text-sm font-medium text-slate-700">Senha</label>
              </div>
              <div class="relative">
                <input
                  [type]="showPassword() ? 'text' : 'password'"
                  name="password"
                  [(ngModel)]="password"
                  placeholder="••••••••"
                  autocomplete="current-password"
                  class="w-full px-3.5 py-2.5 pr-10 rounded-lg border border-slate-300 bg-white text-sm text-slate-900
                         placeholder-slate-400 outline-none transition
                         focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20"
                />
                <button
                  type="button"
                  (click)="showPassword.update(v => !v)"
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors">
                  @if (showPassword()) {
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                    </svg>
                  } @else {
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                  }
                </button>
              </div>
            </div>

            <!-- Botão -->
            <button
              type="submit"
              [disabled]="loading()"
              class="w-full mt-1 py-2.5 px-4 bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400
                     text-white text-sm font-semibold rounded-lg transition-colors
                     focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2
                     flex items-center justify-center gap-2">
              @if (loading()) {
                <svg class="animate-spin w-4 h-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
                </svg>
                Entrando...
              } @else {
                Entrar
              }
            </button>

          </form>
        </div>
      </div>

    </div>
  `
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  loading = signal(false);
  error = signal('');
  showPassword = signal(false);

  onSubmit(): void {
    if (!this.username || !this.password) return;

    this.loading.set(true);
    this.error.set('');

    this.auth.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/projects']),
      error: err => {
        this.loading.set(false);
        if (err.status === 401) {
          this.error.set('Usuário ou senha incorretos.');
        } else {
          this.error.set('Erro ao conectar. Tente novamente.');
        }
      }
    });
  }
}
