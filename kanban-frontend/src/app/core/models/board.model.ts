export interface Attachment {
  id: number;
  fileName: string;
  blobUrl: string;
  uploadedAt: string;
}

export interface Card {
  id: number;
  title: string;
  description: string;
  columnId: number;
  assigneeId: string | null;
  dueDate: string | null;
  priority: string;
  position: number;
  createdAt: string;
  attachments: Attachment[];
}

export interface CardSummary {
  id: number;
  title: string;
  priority: string;
  position: number;
  assigneeId: string | null;
  dueDate: string | null;
}

export interface Column {
  id: number;
  name: string;
  color: string;
  position: number;
  projectId: number;
  cards: CardSummary[];
}

export interface CreateColumnRequest {
  name: string;
  color?: string;
  position?: number;
}

export interface CreateCardRequest {
  columnId: number;
  title: string;
  description?: string;
  assigneeId?: string;
  dueDate?: string;
  priority?: string;
  position?: number;
}
