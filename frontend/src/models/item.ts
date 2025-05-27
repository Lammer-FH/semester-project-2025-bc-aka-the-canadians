export interface Item {
  id: number;
  name: string;
  description: string;
  location: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  imageUrl?: string;
  imageData?: string; // Base64 encoded image for upload
}

export interface ItemFilters {
  status?: string;
  location?: string;
  search?: string;
} 