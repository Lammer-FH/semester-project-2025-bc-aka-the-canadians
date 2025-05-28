import axios from 'axios';
import { Item, ItemFilters } from '@/models/item';

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/items`;

export const itemService = {
	async getAllItems(filters: ItemFilters = {}): Promise<Item[]> {
		try {
			const params = new URLSearchParams();
			if (filters.status) params.append('status', filters.status.toUpperCase());
			if (filters.location) params.append('location', filters.location);
			if (filters.search) params.append('search', filters.search);

			const response = await axios.get<Item[]>(API_URL, { params });
			return response.data;
		} catch (error) {
			console.error('Error fetching items:', error);
			throw error;
		}
	},

	async getItemById(id: number): Promise<Item> {
		try {
			const response = await axios.get<Item>(`${API_URL}/${id}`);
			return response.data;
		} catch (error) {
			console.error('Error fetching item:', error);
			throw error;
		}
	},

	async createItem(
		itemData: Omit<Item, 'id' | 'createdAt' | 'updatedAt'>
	): Promise<Item> {
		try {
			const response = await axios.post<Item>(API_URL, itemData);
			return response.data;
		} catch (error) {
			console.error('Error creating item:', error);
			throw error;
		}
	},

	async updateItem(id: number, itemData: Partial<Item>): Promise<Item> {
		try {
			// Remove fields that shouldn't be sent to backend
			const { id: itemId, createdAt, updatedAt, ...updateData } = itemData;

			const response = await axios.put<Item>(`${API_URL}/${id}`, updateData);
			return response.data;
		} catch (error) {
			console.error('Error updating item:', error);
			throw error;
		}
	},

	async deleteItem(id: number): Promise<void> {
		try {
			await axios.delete(`${API_URL}/${id}`);
		} catch (error) {
			console.error('Error deleting item:', error);
			throw error;
		}
	},
};
