import axios from "axios";
import {
  Location,
  LocationCreateData,
  LocationUpdateData,
} from "@/models/location";
import { Item } from "@/models/item";

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/locations`;

export const locationService = {
  async getAllLocations(): Promise<Location[]> {
    try {
      const response = await axios.get<Location[]>(API_URL, {});
      return response.data;
    } catch (error) {
      console.error("Error fetching locations:", error);
      throw error;
    }
  },

  async getLocationById(id: number): Promise<Location> {
    try {
      const response = await axios.get<Location>(`${API_URL}/${id}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching location:", error);
      throw error;
    }
  },

  async getItemsByLocation(locationId: number): Promise<Item[]> {
    try {
      const response = await axios.get<Item[]>(
        `${API_URL}/${locationId}/items`
      );
      return response.data;
    } catch (error) {
      console.error("Error fetching items by location:", error);
      throw error;
    }
  },

  async createLocation(locationData: LocationCreateData): Promise<Location> {
    try {
      const response = await axios.post<Location>(API_URL, locationData);
      return response.data;
    } catch (error) {
      console.error("Error creating location:", error);
      throw error;
    }
  },

  async updateLocation(
    id: number,
    locationData: LocationUpdateData
  ): Promise<Location> {
    try {
      const response = await axios.put<Location>(
        `${API_URL}/${id}`,
        locationData
      );
      return response.data;
    } catch (error) {
      console.error("Error updating location:", error);
      throw error;
    }
  },

  async deleteLocation(id: number): Promise<void> {
    try {
      await axios.delete(`${API_URL}/${id}`);
    } catch (error) {
      console.error("Error deleting location:", error);
      throw error;
    }
  },
};
