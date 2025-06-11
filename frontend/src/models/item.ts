import type { Report, ReportStatus, ReportType } from "./report";
import type { User } from "./user";

export interface Item {
    id: number;
    name: string;
    description?: string;
    reportId: number;
    claimedByUserId?: number;
    createdAt: string;
    status: ItemStatus;
    locationId?: number;
    locationName?: string;
    reportStatus?: ReportStatus;
    reportType?: ReportType
    reporterUserId?: number;
    reporterUserName?: string;
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

export enum ItemStatus {
    CLAIMED = "CLAIMED",
    UNCLAIMED = "UNCLAIMED"
}
