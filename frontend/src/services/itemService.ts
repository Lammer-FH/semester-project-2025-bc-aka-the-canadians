import axios from 'axios';
import { Item, ItemFilters } from '@/models/item';

const API_URL = 'http://localhost:8080/api/v1/items';

export const itemService = {
    async getAllItems(filters: ItemFilters = {}): Promise<Item[]> {
        const response = await axios.get<Item[]>(API_URL, { params: filters });
        return response.data;
    },

    async getItemById(id: number): Promise<Item> {
        const response = await axios.get<Item>(`${API_URL}/${id}`);
        return response.data;
    },

    async createItem(itemData: Omit<Item, 'id'>): Promise<Item> {
        const response = await axios.post<Item>(API_URL, itemData);
        return response.data;
    },

    async updateItem(id: number, itemData: Partial<Item>): Promise<Item> {
        const response = await axios.put<Item>(`${API_URL}/${id}`, itemData);
        return response.data;
    },

    async deleteItem(id: number): Promise<void> {
        await axios.delete(`${API_URL}/${id}`);
    }
}; 