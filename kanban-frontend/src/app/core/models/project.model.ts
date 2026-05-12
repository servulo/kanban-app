export interface Member {
  keycloakId: string;
  role: string;
}

export interface Project {
  id: number;
  name: string;
  description: string;
  ownerId: string;
  createdAt: string;
  members: Member[];
}

export interface CreateProjectRequest {
  name: string;
  description: string;
}
