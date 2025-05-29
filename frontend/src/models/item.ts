import { Location } from './location';
import { User } from './user';

export interface Item {
	id: number;
	name: string;
	description: string;
	category: string;
	imageUrl?: string;
	dateReported: string;
	location: Location;
	status: ItemStatus;
	reportedBy: User;
}

export enum ItemStatus {
	LOST = 'LOST',
	FOUND = 'FOUND',
	CLAIMED = 'CLAIMED',
	RETURNED = 'RETURNED',
}

export interface ItemFilters {
	status?: ItemStatus;
	category?: string;
	locationId?: number;
}
