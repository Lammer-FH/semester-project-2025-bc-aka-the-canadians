export interface Location {
    id: number;
    name: string;
    description: string;
    building: string;
    floor: string;
    roomNumber: string;
    latitude?: number;
    longitude?: number;
    createdAt: string;
    updatedAt: string;
}

export interface LocationCreateData {
    name: string;
    description: string;
    building: string;
    floor: string;
    room: string;
}

export interface LocationUpdateData {
    name?: string;
    description?: string;
    building?: string;
    floor?: string;
    room?: string;
}
