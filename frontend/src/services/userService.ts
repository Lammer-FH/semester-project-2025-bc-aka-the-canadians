import axios from "axios";
import { User, UserCreateData, UserUpdateData } from "@/models/user";

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/users`;

export const userService = {
    async getUserById(id: number): Promise<User> {
        try {
            const response = await axios.get<User>(`${API_URL}/${id}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching user:", error);
            throw error;
        }
    },

    async getUserByEmail(email: string): Promise<User | null> {
        try {
            const response = await axios.get<User>(`${API_URL}/email/${email}`);
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error) && error.response?.status === 404) {
                return null; // User not found
            }
            console.error("Error fetching user by email:", error);
            throw error;
        }
    }, async createUser(userData: UserCreateData): Promise<User> {
        try {
            const response = await axios.post<User>(API_URL, userData);
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error)) {
                if (error.response?.status === 409) {
                    // Conflict - user already exists
                    const message = typeof error.response.data === 'string'
                        ? error.response.data
                        : 'A user with this email already exists';
                    throw new Error(message);
                }
            }
            console.error("Error creating user:", error);
            throw error;
        }
    }, async updateUser(id: number, userData: UserUpdateData): Promise<User> {
        try {
            const response = await axios.put<User>(
                `${API_URL}/${id}`,
                userData,
            );
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error)) {
                if (error.response?.status === 409) {
                    // Conflict - email already exists
                    const message = typeof error.response.data === 'string'
                        ? error.response.data
                        : 'Email address is already in use by another user';
                    throw new Error(message);
                }
            }
            console.error("Error updating user:", error);
            throw error;
        }
    },

    async deleteUser(id: number): Promise<void> {
        try {
            await axios.delete(`${API_URL}/${id}`);
        } catch (error) {
            console.error("Error deleting user:", error);
            throw error;
        }
    },
};
