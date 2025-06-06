import axios from "axios";
import {
    Report,
    ReportCreateData,
    ReportUpdateData,
    ReportStatus,
    ReportType,
} from "@/models/report";

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/reports`;

export const reportService = {
    async getAllReports(
        status?: ReportStatus,
        type?: ReportType,
    ): Promise<Report[]> {
        try {
            const response = await axios.get<Report[]>(API_URL, {
                params: { status, type },
            });
            return response.data;
        } catch (error) {
            console.error("Error fetching reports:", error);
            throw error;
        }
    },

    async getReportById(id: number): Promise<Report> {
        try {
            const response = await axios.get<Report>(`${API_URL}/${id}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching report:", error);
            throw error;
        }
    },

    async getReportsByStatus(status: ReportStatus): Promise<Report[]> {
        try {
            const response = await axios.get<Report[]>(`${API_URL}/status/${status}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching reports by status:", error);
            throw error;
        }
    },

    async getReportsByUser(userId: number): Promise<Report[]> {
        try {
            const response = await axios.get<Report[]>(`${API_URL}/user/${userId}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching reports by user:", error);
            throw error;
        }
    },

    async createReport(reportData: ReportCreateData): Promise<Report> {
        try {
            const response = await axios.post<Report>(API_URL, reportData);
            return response.data;
        } catch (error) {
            console.error("Error creating report:", error);
            throw error;
        }
    },

    async updateReport(
        id: number,
        reportData: ReportUpdateData,
    ): Promise<Report> {
        try {
            const response = await axios.put<Report>(
                `${API_URL}/${id}`,
                reportData,
            );
            return response.data;
        } catch (error) {
            console.error("Error updating report:", error);
            throw error;
        }
    },

    async updateReportStatus(id: number, status: ReportStatus): Promise<Report> {
        try {
            const response = await axios.put<Report>(`${API_URL}/${id}/status`, null, {
                params: { status }
            });
            return response.data;
        } catch (error) {
            console.error("Error updating report status:", error);
            throw error;
        }
    },

    async deleteReport(id: number): Promise<void> {
        try {
            await axios.delete(`${API_URL}/${id}`);
        } catch (error) {
            console.error("Error deleting report:", error);
            throw error;
        }
    },

    async searchReports(query: string): Promise<Report[]> {
        try {
            const response = await axios.get<Report[]>(`${API_URL}/search`, {
                params: { query }
            });
            return response.data;
        } catch (error) {
            console.error("Error searching reports:", error);
            throw error;
        }
    }
};
