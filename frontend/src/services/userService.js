import axios from 'axios';

const API_URL = 'http://localhost:8080/api/users';

export const userService = {
    async createUser(userData) {
        const response = await axios.post(API_URL, userData);
        return response.data;
    },

    async updateUser(id, userData) {
        const response = await axios.put(`${API_URL}/${id}`, userData);
        return response.data;
    }
}; 