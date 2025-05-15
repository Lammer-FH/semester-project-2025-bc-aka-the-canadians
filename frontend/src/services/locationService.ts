import axios from 'axios';
import { Location, LocationCreateData, LocationUpdateData } from '@/models/location';
import { Item } from '@/models/item';

const API_URL = 'http://localhost:8080/api/v1/locations';

export const locationService = {
    async getAllLocations(building?: string): Promise<Location[]> {
        const response = await axios.get<Location[]>(API_URL, { params: { building } });
        return response.data;
    },

    async getLocationById(id: number): Promise<Location> {
        const response = await axios.get<Location>(`${API_URL}/${id}`);
        return response.data;
    },

    async getItemsByLocation(locationId: number): Promise<Item[]> {
        const response = await axios.get<Item[]>(`${API_URL}/${locationId}/items`);
        return response.data;
    },

    async createLocation(locationData: LocationCreateData): Promise<Location> {
        const response = await axios.post<Location>(API_URL, locationData);
        return response.data;
    },

    async updateLocation(id: number, locationData: LocationUpdateData): Promise<Location> {
        const response = await axios.put<Location>(`${API_URL}/${id}`, locationData);
        return response.data;
    },

    async deleteLocation(id: number): Promise<void> {
        await axios.delete(`${API_URL}/${id}`);
    }
}; 