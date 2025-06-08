import axios from "axios";
import { Report, ReportCreateData, ReportUpdateData } from "@/models/report";

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/reports`;

export const reportService = {
    async getAllReports(): Promise<Report[]> {
        const response = await axios.get<Report[]>(API_URL);
        return response.data;
    },

    async getReportById(id: number): Promise<Report> {
        const response = await axios.get<Report>(`${API_URL}/${id}`);
        return response.data;
    },

    async createReport(reportData: ReportCreateData): Promise<Report> {
        const response = await axios.post<Report>(API_URL, reportData);
        return response.data;
    },

    async updateReport(
        id: number,
        reportData: ReportUpdateData
    ): Promise<Report> {
        try {
            // Prepare data in the format expected by backend
            const payload = {
                locationId: reportData.locationId,
                status: reportData.status,
            };

            const response = await axios.put<Report>(
                `${API_URL}/${id}`,
                payload
            );
            return response.data;
        } catch (error) {
            console.error("Error updating report:", error);
            throw error;
        }
    },

    async deleteReport(id: number): Promise<void> {
        await axios.delete(`${API_URL}/${id}`);
    },
};
