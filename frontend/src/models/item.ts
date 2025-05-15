export interface Item {
  id: number;
  name: string;
  description: string;
  location: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface ItemFilters {
  status?: string;
  location?: string;
  search?: string;
} 