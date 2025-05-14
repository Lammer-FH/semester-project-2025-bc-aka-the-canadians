import axios from 'axios';

const API_URL = 'http://localhost:8080/api/reports';

export const reportService = {
    async getReportById(id) {
        const response = await axios.get(`${API_URL}/${id}`);
        return response.data;
    },

    async createReport(reportData) {
        const response = await axios.post(API_URL, reportData);
        return response.data;
    },

    async updateReport(id, reportData) {
        const response = await axios.put(`${API_URL}/${id}`, reportData);
        return response.data;
    },

    async deleteReport(id) {
        await axios.delete(`${API_URL}/${id}`);
    }
}; 