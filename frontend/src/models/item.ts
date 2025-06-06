import type { Location } from "./location";
import type { User } from "./user";
import type { Report } from "./report";

export enum ItemStatus {
    LOST = "LOST",
    FOUND = "FOUND",
    CLAIMED = "CLAIMED",
    RETURNED = "RETURNED",
}

export interface Item {
    id: number;
    name: string;
    description?: string;
    category?: string;
    imageUrl?: string;
    status: ItemStatus;
    createdAt: string;
    report: Report;
    claimedByUser?: User;
}

export interface ItemCreateData {
    name: string;
    description?: string;
    category?: string;
    imageUrl?: string;
    status?: ItemStatus;
    reportId: number;
}

export interface ItemUpdateData {
    name?: string;
    description?: string;
    category?: string;
    imageUrl?: string;
    status?: ItemStatus;
    claimedByUserId?: number;
}

export interface ItemFilters {
    status?: ItemStatus;
    category?: string;
    search?: string;
    userId?: number;
    claimedByUserId?: number;
    reportId?: number;
}
