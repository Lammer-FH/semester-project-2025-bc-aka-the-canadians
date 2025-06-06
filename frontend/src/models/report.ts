import type { User } from './user';
import type { Location } from './location';
import type { Item } from './item';

export enum ReportStatus {
    OPEN = "OPEN",
    IN_PROGRESS = "IN_PROGRESS",
    RESOLVED = "RESOLVED",
    CLOSED = "CLOSED",
}

export enum ReportType {
    LOST = "LOST",
    FOUND = "FOUND",
}

export interface Report {
    id: number;
    title: string;
    description: string;
    status: ReportStatus;
    type: ReportType;
    dateCreated: string;
    userId: number;
    itemId?: number;
    user: User;
    location: Location;
    items?: Item[];
}

export interface ReportCreateData {
    userId: number;
    locationId: number;
    status?: ReportStatus;
}

export interface ReportUpdateData {
    status?: ReportStatus;
    locationId?: number;
}
