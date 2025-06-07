import type { User } from "./user";
import type { Location } from "./location";
import type { Item } from "./item";

export interface Report {
    id: number;
    userId: number;
    locationId: number;
    createdAt: string;
    status: boolean;
    itemIds?: number[];
    user?: User;
    location?: Location;
    items?: Item[];
}

export interface ReportCreateData {
    userId: number;
    locationId: number;
    status?: boolean;
}

export interface ReportUpdateData {
    userId?: number;
    locationId?: number;
    status?: boolean;
}

export interface ReportFilters {
    userId?: number;
    locationId?: number;
    status?: boolean;
}
