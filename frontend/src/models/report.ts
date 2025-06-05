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
}

export type ReportCreateData = Omit<Report, "id" | "dateCreated">;
export type ReportUpdateData = Partial<ReportCreateData>;
