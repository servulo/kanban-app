import { Component, inject, signal, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ProjectService } from '../../core/services/project.service';
import { AuthService } from '../../core/services/auth.service';
import { Project } from '../../core/models/project.model';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';

@Component({
  selector: 'app-projects',
  imports: [FormsModule, DatePipe, NavbarComponent],
  template: `
    <div class="min-h-screen bg-gray-50 flex flex-col">
      <app-navbar />

      <main class="flex-1 px-6 py-8 max-w-6xl mx-auto w-full">

        <!-- Header -->
        <div class="flex items-center justify-between mb-8">
          <div>
            <h1 class="text-2xl font-bold text-gray-900">Meus Projetos</h1>
            <p class="text-sm text-gray-500 mt-0.5">{{ projects().length }} projeto(s)</p>
          </div>
          <button
            (click)="openModal()"
            class="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold rounded-lg transition-colors shadow-sm">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            Novo Projeto
          </button>
        </div>

        <!-- Loading -->
        @if (loading()) {
          <div class="flex items-center justify-center py-20">
            <svg class="animate-spin w-8 h-8 text-indigo-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
            </svg>
          </div>
        }

        <!-- Empty state -->
        @if (!loading() && projects().length === 0) {
          <div class="text-center py-20">
            <div class="inline-flex items-center justify-center w-16 h-16 bg-indigo-100 rounded-2xl mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 text-indigo-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                  d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-800 mb-1">Nenhum projeto ainda</h3>
            <p class="text-gray-400 text-sm mb-6">Crie seu primeiro projeto para começar</p>
            <button
              (click)="openModal()"
              class="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold rounded-lg transition-colors">
              Criar Projeto
            </button>
          </div>
        }

        <!-- Grid -->
        @if (!loading() && projects().length > 0) {
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            @for (p of projects(); track p.id) {
              <div
                (click)="goToBoard(p)"
                class="bg-white rounded-xl border border-gray-100 p-5 cursor-pointer
                       hover:shadow-md hover:border-indigo-200 transition-all group">

                <!-- Color bar -->
                <div class="w-8 h-1.5 bg-indigo-500 rounded-full mb-4 group-hover:w-12 transition-all"></div>

                <h3 class="font-semibold text-gray-900 mb-1 truncate">{{ p.name }}</h3>
                <p class="text-sm text-gray-500 mb-4 line-clamp-2 min-h-[2.5rem]">
                  {{ p.description || 'Sem descrição' }}
                </p>

                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-1.5 text-xs text-gray-400">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                    {{ p.members.length }} membro(s)
                  </div>
                  <div class="flex items-center gap-1.5 text-xs text-gray-400">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    {{ p.createdAt | date:'dd/MM/yy' }}
                  </div>
                </div>

                <!-- Actions -->
                <div class="flex items-center gap-2 mt-4 pt-4 border-t border-gray-50 opacity-0 group-hover:opacity-100 transition-opacity"
                     (click)="$event.stopPropagation()">
                  <button
                    (click)="openEditModal(p)"
                    class="flex-1 text-xs py-1.5 text-gray-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-md transition-colors font-medium">
                    Editar
                  </button>
                  <button
                    (click)="deleteProject(p)"
                    class="flex-1 text-xs py-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-md transition-colors font-medium">
                    Excluir
                  </button>
                </div>
              </div>
            }
          </div>
        }
      </main>

      <!-- Modal -->
      @if (showModal()) {
        <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50 px-4" (click)="closeModal()">
          <div class="bg-white rounded-2xl shadow-xl w-full max-w-md p-6" (click)="$event.stopPropagation()">
            <h2 class="text-lg font-bold text-gray-900 mb-6">
              {{ editingProject() ? 'Editar Projeto' : 'Novo Projeto' }}
            </h2>

            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Nome *</label>
              <input
                type="text"
                [(ngModel)]="formName"
                placeholder="Nome do projeto"
                maxlength="100"
                class="w-full px-3.5 py-2.5 rounded-lg border border-gray-200 text-sm
                       focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              />
            </div>

            <div class="mb-6">
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Descrição</label>
              <textarea
                [(ngModel)]="formDescription"
                placeholder="Descreva o projeto..."
                rows="3"
                class="w-full px-3.5 py-2.5 rounded-lg border border-gray-200 text-sm resize-none
                       focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent">
              </textarea>
            </div>

            @if (modalError()) {
              <p class="text-sm text-red-500 mb-4">{{ modalError() }}</p>
            }

            <div class="flex gap-3">
              <button
                (click)="closeModal()"
                class="flex-1 py-2.5 border border-gray-200 text-sm font-medium text-gray-600 rounded-lg hover:bg-gray-50 transition-colors">
                Cancelar
              </button>
              <button
                (click)="saveProject()"
                [disabled]="saving()"
                class="flex-1 py-2.5 bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400
                       text-white text-sm font-semibold rounded-lg transition-colors">
                {{ saving() ? 'Salvando...' : (editingProject() ? 'Salvar' : 'Criar') }}
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class ProjectsComponent implements OnInit {
  private projectService = inject(ProjectService);
  private auth = inject(AuthService);
  private router = inject(Router);

  projects = signal<Project[]>([]);
  loading = signal(true);
  showModal = signal(false);
  editingProject = signal<Project | null>(null);
  saving = signal(false);
  modalError = signal('');

  formName = '';
  formDescription = '';

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.loading.set(true);
    this.projectService.list().subscribe({
      next: ps => {
        this.projects.set(ps);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  goToBoard(p: Project): void {
    this.router.navigate(['/projects', p.id, 'board']);
  }

  openModal(): void {
    this.editingProject.set(null);
    this.formName = '';
    this.formDescription = '';
    this.modalError.set('');
    this.showModal.set(true);
  }

  openEditModal(p: Project): void {
    this.editingProject.set(p);
    this.formName = p.name;
    this.formDescription = p.description ?? '';
    this.modalError.set('');
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  saveProject(): void {
    if (!this.formName.trim()) {
      this.modalError.set('O nome é obrigatório.');
      return;
    }

    this.saving.set(true);
    this.modalError.set('');

    const editing = this.editingProject();
    const request = { name: this.formName.trim(), description: this.formDescription.trim() };

    const call = editing
      ? this.projectService.update(editing.id, request)
      : this.projectService.create(request);

    call.subscribe({
      next: () => {
        this.saving.set(false);
        this.closeModal();
        this.loadProjects();
      },
      error: () => {
        this.saving.set(false);
        this.modalError.set('Erro ao salvar. Tente novamente.');
      }
    });
  }

  deleteProject(p: Project): void {
    if (!confirm(`Excluir o projeto "${p.name}"? Esta ação não pode ser desfeita.`)) return;
    this.projectService.delete(p.id).subscribe({
      next: () => this.projects.update(list => list.filter(x => x.id !== p.id)),
      error: () => alert('Erro ao excluir o projeto.')
    });
  }
}
