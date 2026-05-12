import { Component, Input, inject, signal, computed, OnInit, HostListener, ElementRef } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Notification } from '../../../core/models/notification.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, DatePipe],
  template: `
    <nav class="bg-white border-b border-gray-200 px-6 h-14 flex items-center justify-between shrink-0">
      <!-- Left -->
      <div class="flex items-center gap-3">
        <a routerLink="/projects" class="flex items-center gap-2 text-indigo-600 font-bold text-lg hover:text-indigo-700">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          Kanban
        </a>
        @if (projectName) {
          <span class="text-gray-300">/</span>
          <span class="text-gray-700 font-medium text-sm">{{ projectName }}</span>
        }
      </div>

      <!-- Right -->
      <div class="flex items-center gap-2">
        <!-- Notifications -->
        <div class="relative">
          <button
            (click)="toggleNotifications()"
            class="relative p-2 rounded-lg text-gray-500 hover:bg-gray-100 hover:text-gray-700 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
            @if (unreadCount() > 0) {
              <span class="absolute top-1 right-1 w-4 h-4 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                {{ unreadCount() > 9 ? '9+' : unreadCount() }}
              </span>
            }
          </button>

          @if (showNotifications()) {
            <div class="absolute right-0 top-11 w-80 bg-white rounded-xl shadow-lg border border-gray-100 z-50 overflow-hidden">
              <div class="flex items-center justify-between px-4 py-3 border-b border-gray-100">
                <span class="font-semibold text-gray-800 text-sm">Notificações</span>
                @if (unreadCount() > 0) {
                  <button (click)="markAllRead()" class="text-xs text-indigo-600 hover:text-indigo-700 font-medium">
                    Marcar todas como lidas
                  </button>
                }
              </div>
              <div class="max-h-72 overflow-y-auto">
                @if (notifications().length === 0) {
                  <div class="px-4 py-6 text-center text-gray-400 text-sm">
                    Nenhuma notificação
                  </div>
                }
                @for (n of notifications(); track n.id) {
                  <div
                    (click)="markRead(n)"
                    class="px-4 py-3 border-b border-gray-50 cursor-pointer hover:bg-gray-50 transition-colors"
                    [class.bg-indigo-50]="!n.isRead">
                    <p class="text-sm text-gray-800" [class.font-medium]="!n.isRead">{{ n.message }}</p>
                    <p class="text-xs text-gray-400 mt-1">{{ n.createdAt | date:'dd/MM/yy HH:mm' }}</p>
                  </div>
                }
              </div>
            </div>
          }
        </div>

        <!-- Logout -->
        <button
          (click)="logout()"
          class="flex items-center gap-1.5 px-3 py-1.5 text-sm text-gray-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          Sair
        </button>
      </div>
    </nav>
  `
})
export class NavbarComponent implements OnInit {
  @Input() projectName = '';

  private auth = inject(AuthService);
  private notifService = inject(NotificationService);
  private elementRef = inject(ElementRef);

  notifications = signal<Notification[]>([]);
  showNotifications = signal(false);
  unreadCount = computed(() => this.notifications().filter(n => !n.isRead).length);

  ngOnInit(): void {
    this.loadNotifications();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showNotifications.set(false);
    }
  }

  toggleNotifications(): void {
    this.showNotifications.update(v => !v);
    if (this.showNotifications()) {
      this.loadNotifications();
    }
  }

  loadNotifications(): void {
    this.notifService.list().subscribe({
      next: ns => this.notifications.set(ns),
      error: () => {}
    });
  }

  markRead(n: Notification): void {
    if (n.isRead) return;
    this.notifService.markAsRead(n.id).subscribe(() => {
      this.notifications.update(list =>
        list.map(item => item.id === n.id ? { ...item, isRead: true } : item)
      );
    });
  }

  markAllRead(): void {
    this.notifService.markAllAsRead().subscribe(() => {
      this.notifications.update(list => list.map(n => ({ ...n, isRead: true })));
    });
  }

  logout(): void {
    this.auth.logout();
  }
}
