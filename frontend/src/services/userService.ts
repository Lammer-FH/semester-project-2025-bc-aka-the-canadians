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

    async createUser(userData: UserCreateData): Promise<User> {
        try {
            const response = await axios.post<User>(API_URL, userData);
            return response.data;
        } catch (error) {
            console.error("Error creating user:", error);
            throw error;
        }
    },

    async updateUser(id: number, userData: UserUpdateData): Promise<User> {
        try {
            const response = await axios.put<User>(
                `${API_URL}/${id}`,
                userData,
            );
            return response.data;
        } catch (error) {
            console.error("Error updating user:", error);
            throw error;
        }
    },
};
