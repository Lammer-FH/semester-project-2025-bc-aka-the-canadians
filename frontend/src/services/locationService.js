import axios from 'axios';

const API_URL = 'http://localhost:8080/api/locations';

export const locationService = {
    async getAllLocations() {
        const response = await axios.get(API_URL);
        return response.data;
    },

    async getItemsByLocation(locationId) {
        const response = await axios.get(`${API_URL}/${locationId}/items`);
        return response.data;
    },

    async createLocation(locationData) {
        const response = await axios.post(API_URL, locationData);
        return response.data;
    },

    async updateLocation(id, locationData) {
        const response = await axios.put(`${API_URL}/${id}`, locationData);
        return response.data;
    },

    async deleteLocation(id) {
        await axios.delete(`${API_URL}/${id}`);
    }
}; 