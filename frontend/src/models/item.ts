import type { Report } from "./report";
import type { User } from "./user";

export interface Item {
    id: number;
    name: string;
    description?: string;
    reportId: number;
    createdAt: string;
    report?: Report;
    claimedByUser?: User;
}

export interface ItemCreateData {
    name: string;
    description?: string;
    reportId: number;
}

export interface ItemUpdateData {
    name?: string;
    description?: string;
}

export interface ItemFilters {
    reportId?: number;
    claimedByUserId?: number;
}
