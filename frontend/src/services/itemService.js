import axios from 'axios';

const API_URL = 'http://localhost:8080/api/items';

export const itemService = {
    async getAllItems(filters = {}) {
        const response = await axios.get(API_URL, { params: filters });
        return response.data;
    },

    async getItemById(id) {
        const response = await axios.get(`${API_URL}/${id}`);
        return response.data;
    },

    async updateItem(id, itemData) {
        const response = await axios.put(`${API_URL}/${id}`, itemData);
        return response.data;
    },

    async deleteItem(id) {
        await axios.delete(`${API_URL}/${id}`);
    }
}; 