import axios from "axios";
import { Item, ItemFilters } from "@/models/item";

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/items`;

export const itemService = {
    async getAllItems(filters: ItemFilters = {}): Promise<Item[]> {
        try {
            const params = new URLSearchParams();
            if (filters.status)
                params.append("status", filters.status.toUpperCase());
            if (filters.location) params.append("location", filters.location);
            if (filters.search) params.append("search", filters.search);

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

    async createItem(
        itemData: Omit<Item, "id" | "createdAt" | "updatedAt">,
    ): Promise<Item> {
        try {
            const { imageData, ...itemPayload } = itemData;

            const response = await axios.post<Item>(API_URL, itemPayload);
            const newItem = response.data;

            if (imageData) {
                try {
                    const base64Response = await fetch(imageData);
                    const blob = await base64Response.blob();

                    const formData = new FormData();
                    formData.append("file", blob, `item-${newItem.id}.jpg`);
                    formData.append("itemId", newItem.id.toString());

                    await axios.post(
                        `${API_URL}/${newItem.id}/image`,
                        formData,
                        {
                            headers: {
                                "Content-Type": "multipart/form-data",
                            },
                        },
                    );

                    return await this.getItemById(newItem.id);
                } catch (imageError) {
                    console.warn("Error uploading image:", imageError);
                    return newItem;
                }
            }

            return newItem;
        } catch (error) {
            console.error("Error creating item:", error);
            throw error;
        }
    },

    async updateItem(id: number, itemData: Partial<Item>): Promise<Item> {
        try {
            const {
                id: itemId,
                createdAt,
                updatedAt,
                imageData,
                ...updateData
            } = itemData;

            const response = await axios.put<Item>(
                `${API_URL}/${id}`,
                updateData,
            );
            const updatedItem = response.data;

            if (imageData) {
                try {
                    const base64Response = await fetch(imageData);
                    const blob = await base64Response.blob();

                    const formData = new FormData();
                    formData.append("file", blob, `item-${id}.jpg`);

                    await axios.post(`${API_URL}/${id}/image`, formData, {
                        headers: {
                            "Content-Type": "multipart/form-data",
                        },
                    });

                    return await this.getItemById(id);
                } catch (imageError) {
                    console.warn("Error updating image:", imageError);
                    return updatedItem;
                }
            }

            return updatedItem;
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
