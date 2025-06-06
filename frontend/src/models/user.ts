// Import types that are used in User interface
import type { Report } from './report';
import type { Item } from './item';

export interface User {
    id: number;
    name: string;
    email: string;
    createdAt: string;
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
