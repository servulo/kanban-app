import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CdkDragDrop, CdkDropList, CdkDrag, CdkDragPlaceholder, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { ColumnService } from '../../core/services/column.service';
import { CardService } from '../../core/services/card.service';
import { ProjectService } from '../../core/services/project.service';
import { Column, CardSummary } from '../../core/models/board.model';
import { Project } from '../../core/models/project.model';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';

@Component({
  selector: 'app-board',
  imports: [FormsModule, NavbarComponent, CdkDropList, CdkDrag, CdkDragPlaceholder],
  template: `
    <div class="h-screen flex flex-col bg-gray-100">
      <app-navbar [projectName]="project()?.name ?? ''" />

      @if (loading()) {
        <div class="flex-1 flex items-center justify-center">
          <svg class="animate-spin w-8 h-8 text-indigo-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
          </svg>
        </div>
      }

      @if (!loading()) {
        <div class="flex-1 flex gap-4 overflow-x-auto px-6 py-5 min-h-0">

          <!-- Columns -->
          @for (col of columns(); track col.id) {
            <div class="flex flex-col w-72 shrink-0">
              <!-- Column header -->
              <div class="flex items-center justify-between mb-3 px-1">
                <div class="flex items-center gap-2">
                  <div class="w-3 h-3 rounded-full" [style.background]="col.color"></div>
                  <span class="font-semibold text-gray-700 text-sm">{{ col.name }}</span>
                  <span class="text-xs text-gray-400 bg-gray-200 rounded-full px-1.5 py-0.5">{{ col.cards.length }}</span>
                </div>
                <button
                  (click)="deleteColumn(col)"
                  class="p-1 text-gray-300 hover:text-red-400 transition-colors rounded">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                  </svg>
                </button>
              </div>

              <!-- Cards list -->
              <div
                cdkDropList
                [id]="'col-' + col.id"
                [cdkDropListData]="col.cards"
                [cdkDropListConnectedTo]="connectedLists()"
                (cdkDropListDropped)="onDrop($event, col)"
                class="flex flex-col gap-2 flex-1 min-h-[4rem] rounded-xl p-2 transition-colors"
                [class.bg-indigo-50]="false">

                @for (card of col.cards; track card.id) {
                  <div
                    cdkDrag
                    (click)="openCardModal(card, col)"
                    class="bg-white rounded-lg p-3 shadow-sm border border-gray-100 cursor-pointer
                           hover:shadow-md hover:border-indigo-200 transition-all group">

                    <!-- Priority badge -->
                    <div class="flex items-center justify-between mb-2">
                      <span class="text-xs font-medium px-2 py-0.5 rounded-full"
                        [class]="priorityClass(card.priority)">
                        {{ priorityLabel(card.priority) }}
                      </span>
                      <button
                        (click)="$event.stopPropagation(); deleteCard(card, col)"
                        class="opacity-0 group-hover:opacity-100 p-0.5 text-gray-300 hover:text-red-400 transition-all">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                        </svg>
                      </button>
                    </div>

                    <p class="text-sm font-medium text-gray-800 leading-snug">{{ card.title }}</p>

                    <!-- Due date -->
                    @if (card.dueDate) {
                      <div class="flex items-center gap-1 mt-2 text-xs text-gray-400">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                        </svg>
                        {{ card.dueDate }}
                      </div>
                    }

                    <!-- Drag handle placeholder -->
                    <div *cdkDragPlaceholder class="bg-indigo-100 rounded-lg h-16 border-2 border-dashed border-indigo-300"></div>
                  </div>
                }
              </div>

              <!-- Add card -->
              @if (addingCardToColumn() === col.id) {
                <div class="mt-2 bg-white rounded-lg border border-indigo-200 p-3 shadow-sm">
                  <input
                    type="text"
                    [(ngModel)]="newCardTitle"
                    placeholder="Título do card..."
                    (keyup.enter)="saveNewCard(col)"
                    (keyup.escape)="cancelAddCard()"
                    autofocus
                    class="w-full text-sm text-gray-800 placeholder-gray-400 border-none outline-none mb-2"
                  />
                  <div class="flex gap-2">
                    <button
                      (click)="saveNewCard(col)"
                      class="px-3 py-1.5 bg-indigo-600 text-white text-xs font-semibold rounded-md hover:bg-indigo-700 transition-colors">
                      Adicionar
                    </button>
                    <button
                      (click)="cancelAddCard()"
                      class="px-3 py-1.5 text-gray-500 text-xs hover:text-gray-700 rounded-md hover:bg-gray-100 transition-colors">
                      Cancelar
                    </button>
                  </div>
                </div>
              } @else {
                <button
                  (click)="startAddCard(col.id)"
                  class="mt-2 flex items-center gap-1.5 w-full px-3 py-2 text-sm text-gray-400
                         hover:text-gray-600 hover:bg-white rounded-lg transition-colors">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                  </svg>
                  Adicionar card
                </button>
              }
            </div>
          }

          <!-- Add column -->
          <div class="w-72 shrink-0">
            @if (addingColumn()) {
              <div class="bg-white rounded-xl border border-indigo-200 p-4 shadow-sm">
                <input
                  type="text"
                  [(ngModel)]="newColumnName"
                  placeholder="Nome da coluna..."
                  (keyup.enter)="saveNewColumn()"
                  (keyup.escape)="cancelAddColumn()"
                  autofocus
                  class="w-full text-sm font-semibold text-gray-800 placeholder-gray-400 border-none outline-none mb-3"
                />
                <div class="flex items-center gap-2 mb-3">
                  <label class="text-xs text-gray-500">Cor:</label>
                  <input type="color" [(ngModel)]="newColumnColor" class="w-8 h-8 rounded cursor-pointer border-0" />
                </div>
                <div class="flex gap-2">
                  <button
                    (click)="saveNewColumn()"
                    class="px-3 py-1.5 bg-indigo-600 text-white text-xs font-semibold rounded-md hover:bg-indigo-700 transition-colors">
                    Criar
                  </button>
                  <button
                    (click)="cancelAddColumn()"
                    class="px-3 py-1.5 text-gray-500 text-xs hover:text-gray-700 rounded-md hover:bg-gray-100 transition-colors">
                    Cancelar
                  </button>
                </div>
              </div>
            } @else {
              <button
                (click)="addingColumn.set(true)"
                class="flex items-center gap-2 w-full px-4 py-3 bg-white/60 hover:bg-white border-2 border-dashed border-gray-200
                       hover:border-indigo-300 text-gray-400 hover:text-gray-600 text-sm font-medium rounded-xl transition-all">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                </svg>
                Nova coluna
              </button>
            }
          </div>

        </div>
      }

      <!-- Card modal -->
      @if (selectedCard()) {
        <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50 px-4" (click)="closeCardModal()">
          <div class="bg-white rounded-2xl shadow-xl w-full max-w-lg p-6" (click)="$event.stopPropagation()">
            <div class="flex items-center justify-between mb-5">
              <h2 class="text-lg font-bold text-gray-900">Editar Card</h2>
              <button (click)="closeCardModal()" class="text-gray-400 hover:text-gray-600">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
              </button>
            </div>

            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Título *</label>
                <input
                  type="text"
                  [(ngModel)]="editCard.title"
                  class="w-full px-3.5 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Descrição</label>
                <textarea
                  [(ngModel)]="editCard.description"
                  rows="3"
                  class="w-full px-3.5 py-2.5 rounded-lg border border-gray-200 text-sm resize-none focus:outline-none focus:ring-2 focus:ring-indigo-500">
                </textarea>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">Prioridade</label>
                  <select
                    [(ngModel)]="editCard.priority"
                    class="w-full px-3.5 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500">
                    <option value="LOW">Baixa</option>
                    <option value="MEDIUM">Média</option>
                    <option value="HIGH">Alta</option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">Vencimento</label>
                  <input
                    type="date"
                    [(ngModel)]="editCard.dueDate"
                    class="w-full px-3.5 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Responsável (Keycloak ID)</label>
                <input
                  type="text"
                  [(ngModel)]="editCard.assigneeId"
                  placeholder="UUID do usuário"
                  class="w-full px-3.5 py-2.5 rounded-lg border border-gray-200 text-sm font-mono focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
            </div>

            <div class="flex gap-3 mt-6">
              <button
                (click)="closeCardModal()"
                class="flex-1 py-2.5 border border-gray-200 text-sm font-medium text-gray-600 rounded-lg hover:bg-gray-50 transition-colors">
                Cancelar
              </button>
              <button
                (click)="updateCard()"
                [disabled]="savingCard()"
                class="flex-1 py-2.5 bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 text-white text-sm font-semibold rounded-lg transition-colors">
                {{ savingCard() ? 'Salvando...' : 'Salvar' }}
              </button>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class BoardComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private columnService = inject(ColumnService);
  private cardService = inject(CardService);
  private projectService = inject(ProjectService);

  projectId!: number;
  project = signal<Project | null>(null);
  columns = signal<Column[]>([]);
  loading = signal(true);

  addingColumn = signal(false);
  newColumnName = '';
  newColumnColor = '#6366f1';

  addingCardToColumn = signal<number | null>(null);
  newCardTitle = '';

  selectedCard = signal<CardSummary | null>(null);
  selectedCardColumn = signal<Column | null>(null);
  savingCard = signal(false);
  editCard = { title: '', description: '', priority: 'MEDIUM', dueDate: '', assigneeId: '' };

  connectedLists = computed(() =>
    this.columns().map(c => 'col-' + c.id)
  );

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadBoard();
  }

  loadBoard(): void {
    this.projectService.list().subscribe(projects => {
      this.project.set(projects.find(p => p.id === this.projectId) ?? null);
    });

    this.columnService.list(this.projectId).subscribe({
      next: cols => {
        this.columns.set(cols.sort((a, b) => a.position - b.position));
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  // ─── Column ───────────────────────────────────────────────────────────────

  saveNewColumn(): void {
    if (!this.newColumnName.trim()) return;
    const position = this.columns().length;
    this.columnService.create(this.projectId, {
      name: this.newColumnName.trim(),
      color: this.newColumnColor,
      position
    }).subscribe(col => {
      this.columns.update(list => [...list, { ...col, cards: [] }]);
      this.cancelAddColumn();
    });
  }

  cancelAddColumn(): void {
    this.addingColumn.set(false);
    this.newColumnName = '';
    this.newColumnColor = '#6366f1';
  }

  deleteColumn(col: Column): void {
    if (!confirm(`Excluir a coluna "${col.name}" e todos os seus cards?`)) return;
    this.columnService.delete(this.projectId, col.id).subscribe(() => {
      this.columns.update(list => list.filter(c => c.id !== col.id));
    });
  }

  // ─── Card ─────────────────────────────────────────────────────────────────

  startAddCard(columnId: number): void {
    this.addingCardToColumn.set(columnId);
    this.newCardTitle = '';
  }

  cancelAddCard(): void {
    this.addingCardToColumn.set(null);
    this.newCardTitle = '';
  }

  saveNewCard(col: Column): void {
    if (!this.newCardTitle.trim()) return;
    this.cardService.create({
      columnId: col.id,
      title: this.newCardTitle.trim(),
      priority: 'MEDIUM',
      position: col.cards.length
    }).subscribe(card => {
      this.columns.update(list =>
        list.map(c => c.id === col.id
          ? { ...c, cards: [...c.cards, { id: card.id, title: card.title, priority: card.priority, position: card.position, assigneeId: card.assigneeId, dueDate: card.dueDate }] }
          : c)
      );
      this.cancelAddCard();
    });
  }

  deleteCard(card: CardSummary, col: Column): void {
    if (!confirm(`Excluir o card "${card.title}"?`)) return;
    this.cardService.delete(card.id).subscribe(() => {
      this.columns.update(list =>
        list.map(c => c.id === col.id
          ? { ...c, cards: c.cards.filter(x => x.id !== card.id) }
          : c)
      );
    });
  }

  // ─── Card modal ───────────────────────────────────────────────────────────

  openCardModal(card: CardSummary, col: Column): void {
    this.selectedCard.set(card);
    this.selectedCardColumn.set(col);
    this.editCard = {
      title: card.title,
      description: '',
      priority: card.priority,
      dueDate: card.dueDate ?? '',
      assigneeId: card.assigneeId ?? ''
    };
  }

  closeCardModal(): void {
    this.selectedCard.set(null);
    this.selectedCardColumn.set(null);
  }

  updateCard(): void {
    const card = this.selectedCard();
    const col = this.selectedCardColumn();
    if (!card || !col || !this.editCard.title.trim()) return;

    this.savingCard.set(true);
    this.cardService.update(card.id, {
      columnId: col.id,
      title: this.editCard.title,
      description: this.editCard.description,
      priority: this.editCard.priority,
      dueDate: this.editCard.dueDate || undefined,
      assigneeId: this.editCard.assigneeId || undefined,
      position: card.position
    }).subscribe({
      next: updated => {
        this.columns.update(list =>
          list.map(c => c.id === col.id
            ? {
                ...c,
                cards: c.cards.map(x => x.id === card.id
                  ? { ...x, title: updated.title, priority: updated.priority, dueDate: updated.dueDate, assigneeId: updated.assigneeId }
                  : x)
              }
            : c)
        );
        this.savingCard.set(false);
        this.closeCardModal();
      },
      error: () => this.savingCard.set(false)
    });
  }

  // ─── Drag & Drop ──────────────────────────────────────────────────────────

  onDrop(event: CdkDragDrop<CardSummary[]>, targetCol: Column): void {
    if (event.previousContainer === event.container) {
      // Reorder within same column
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      const card = event.container.data[event.currentIndex];
      this.cardService.move(card.id, targetCol.id, event.currentIndex).subscribe();
    } else {
      // Move to another column
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
      const card = event.container.data[event.currentIndex];
      this.cardService.move(card.id, targetCol.id, event.currentIndex).subscribe();
    }
  }

  // ─── Helpers ──────────────────────────────────────────────────────────────

  priorityLabel(priority: string): string {
    const labels: Record<string, string> = { LOW: 'Baixa', MEDIUM: 'Média', HIGH: 'Alta' };
    return labels[priority] ?? priority;
  }

  priorityClass(priority: string): string {
    const classes: Record<string, string> = {
      LOW: 'bg-gray-100 text-gray-500',
      MEDIUM: 'bg-yellow-100 text-yellow-700',
      HIGH: 'bg-red-100 text-red-600'
    };
    return classes[priority] ?? 'bg-gray-100 text-gray-500';
  }
}
