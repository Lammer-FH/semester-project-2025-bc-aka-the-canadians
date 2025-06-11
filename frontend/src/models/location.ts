import type { Report } from "./report";

export interface Location {
  id: number;
  name: string;
  description?: string;
  createdAt: string;
  reportIds?: number[];
  reports?: Report[];
}

export interface LocationCreateData {
  name: string;
  description?: string;
}

export interface LocationUpdateData {
  name?: string;
  description?: string;
}

export interface LocationFilters {
  name?: string;
}
