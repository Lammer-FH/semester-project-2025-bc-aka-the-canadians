import axios from "axios";
import { Item, ItemCreateData, ItemUpdateData, ItemFilters } from "@/models/item";

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/items`;

export const itemService = {
    async getAllItems(filters: ItemFilters = {}): Promise<Item[]> {
        try {
            const params = new URLSearchParams();
            if (filters.status) params.append("status", filters.status);
            if (filters.category) params.append("category", filters.category);
            if (filters.search) params.append("search", filters.search);
            if (filters.userId) params.append("userId", filters.userId.toString());
            if (filters.claimedByUserId) params.append("claimedByUserId", filters.claimedByUserId.toString());

            const response = await axios.get<Item[]>(API_URL, { params });
            return response.data;
        } catch (error) {
            console.error("Error fetching items:", error);
            throw error;
        }
    },

    async getItemById(id: number): Promise<Item> {
        try {
            const response = await axios.get<Item>(`${API_URL}/${id}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching item:", error);
            throw error;
        }
    },

    async getItemsByStatus(status: string): Promise<Item[]> {
        try {
            const response = await axios.get<Item[]>(`${API_URL}/status/${status}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching items by status:", error);
            throw error;
        }
    },

    async getItemsByCategory(category: string): Promise<Item[]> {
        try {
            const response = await axios.get<Item[]>(`${API_URL}/category/${category}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching items by category:", error);
            throw error;
        }
    },

    async getItemsByUser(userId: number): Promise<Item[]> {
        try {
            const response = await axios.get<Item[]>(`${API_URL}/user/${userId}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching items by user:", error);
            throw error;
        }
    },

    async getClaimedItemsByUser(userId: number): Promise<Item[]> {
        try {
            const response = await axios.get<Item[]>(`${API_URL}/claimed/${userId}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching claimed items:", error);
            throw error;
        }
    },

    async createItem(itemData: ItemCreateData): Promise<Item> {
        try {
            const response = await axios.post<Item>(API_URL, itemData);
            return response.data;
        } catch (error) {
            console.error("Error creating item:", error);
            throw error;
        }
    },

    async updateItem(id: number, itemData: ItemUpdateData): Promise<Item> {
        try {
            const response = await axios.put<Item>(`${API_URL}/${id}`, itemData);
            return response.data;
        } catch (error) {
            console.error("Error updating item:", error);
            throw error;
        }
    },

    async deleteItem(id: number): Promise<void> {
        try {
            await axios.delete(`${API_URL}/${id}`);
        } catch (error) {
            console.error("Error deleting item:", error);
            throw error;
        }
    },

    async searchItems(query: string): Promise<Item[]> {
        try {
            const response = await axios.get<Item[]>(`${API_URL}/search`, {
                params: { query }
            });
            return response.data;
        } catch (error) {
            console.error("Error searching items:", error);
            throw error;
        }
    }
};
