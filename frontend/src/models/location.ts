import type { Report } from './report';

export interface Location {
    id: number;
    name: string;
    description?: string;
    building?: string;
    floor?: string;
    roomNumber?: string;
    latitude?: number;
    longitude?: number;
    createdAt: string;
    reports?: Report[];
}

export interface LocationCreateData {
    name: string;
    description?: string;
    building?: string;
    floor?: string;
    roomNumber?: string;
    latitude?: number;
    longitude?: number;
}

export interface LocationUpdateData {
    name?: string;
    description?: string;
    building?: string;
    floor?: string;
    roomNumber?: string;
    latitude?: number;
    longitude?: number;
}
