export interface Notification {
  id: number;
  type: string;
  message: string;
  isRead: boolean;
  createdAt: string;
  relatedEntityId: number | null;
  relatedEntityType: string | null;
}
