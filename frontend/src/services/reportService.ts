import axios from "axios";
import {
    Report,
    ReportCreateData,
    ReportUpdateData,
    ReportStatus,
    ReportType,
} from "@/models/report";

const API_URL = "http://localhost:8080/api/reports";

export const reportService = {
    async getAllReports(
        status?: ReportStatus,
        type?: ReportType,
    ): Promise<Report[]> {
        const response = await axios.get<Report[]>(API_URL, {
            params: { status, type },
        });
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
        reportData: ReportUpdateData,
    ): Promise<Report> {
        const response = await axios.put<Report>(
            `${API_URL}/${id}`,
            reportData,
        );
        return response.data;
    },

    async deleteReport(id: number): Promise<void> {
        await axios.delete(`${API_URL}/${id}`);
    },
};
