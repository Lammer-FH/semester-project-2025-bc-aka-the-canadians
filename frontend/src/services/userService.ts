import axios from 'axios';
import { User, UserCreateData, UserUpdateData } from '@/models/user';

const API_URL = 'http://localhost:8080/api/v1/users';

export const userService = {
    async getUserById(id: number): Promise<User> {
        const response = await axios.get<User>(`${API_URL}/${id}`);
        return response.data;
    },

    async createUser(userData: UserCreateData): Promise<User> {
        const response = await axios.post<User>(API_URL, userData);
        return response.data;
    },

    async updateUser(id: number, userData: UserUpdateData): Promise<User> {
        const response = await axios.put<User>(`${API_URL}/${id}`, userData);
        return response.data;
    }
}; 