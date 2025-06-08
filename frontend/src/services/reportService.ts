import axios from "axios";
import {
    Report,
    ReportCreateData,
    ReportUpdateData,
} from "@/models/report";

const API_URL = `${import.meta.env.VITE_API_URL}/api/reports`;

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
        const response = await axios.put<Report>(
            `${API_URL}/${id}`,
            reportData
        );
        return response.data;
    },

    async deleteReport(id: number): Promise<void> {
        await axios.delete(`${API_URL}/${id}`);
    },
};
