import axios from 'axios';
import { Item, ItemFilters } from '@/models/item';

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/items`;

export const itemService = {
	async getAllItems(filters: ItemFilters = {}): Promise<Item[]> {
		const params = new URLSearchParams();
		if (filters.status) params.append('status', filters.status.toUpperCase());
		if (filters.location) params.append('location', filters.location);

		const response = await axios.get<Item[]>(API_URL, { params });
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
	},
};
