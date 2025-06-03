export interface User {
	id: number;
	username: string;
	email: string;
	firstName: string;
	lastName: string;
	avatar?: string;
	createdAt: string;
	updatedAt: string;
}

export interface UserCreateData {
	username: string;
	email: string;
	password: string;
	firstName: string;
	lastName: string;
}

export interface UserUpdateData {
	email?: string;
	firstName?: string;
	lastName?: string;
}
