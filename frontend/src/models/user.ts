import type { Report } from "./report";
import type { Item } from "./item";

export interface User {
    id: number;
    name: string;
    email: string;
    createdAt: string;
    reportIds?: number[];
    claimedItemIds?: number[];
    reports?: Report[];
    claimedItems?: Item[];
}

export interface UserCreateData {
    name: string;
    email: string;
}

export interface UserUpdateData {
    name?: string;
    email?: string;
}
