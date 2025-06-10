import axios from "axios";
import { Item, ItemFilters, ItemCreateData } from "@/models/item";

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/items`;

export const itemService = {
    async getAllItems(_filters: ItemFilters = {}): Promise<Item[]> {
        try {
            // Backend currently doesn't support query parameters for filtering
            // All filtering is done client-side in the frontend
            const response = await axios.get<Item[]>(API_URL);
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

    async createItem(itemData: ItemCreateData): Promise<Item> {
        try {
            // Prepare data in the format expected by backend
            const payload = {
                name: itemData.name,
                description: itemData.description || "",
                reportId: itemData.reportId,
                status: itemData.status || "UNCLAIMED", // Default to UNCLAIMED if not provided
            };

            const response = await axios.post<Item>(API_URL, payload);
            return response.data;
        } catch (error) {
            console.error("Error creating item:", error);
            throw error;
        }
    },

    async updateItem(id: number, itemData: Partial<Item>): Promise<Item> {
        try {
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
            const {id: itemId, createdAt: createdAt, report: report, claimedByUser: claimedByUser, ...updateData} = itemData;

            const response = await axios.put<Item>(
                `${API_URL}/${id}`,
                updateData
            );
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
};
