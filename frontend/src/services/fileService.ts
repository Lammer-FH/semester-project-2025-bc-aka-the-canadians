import axios from "axios";
import { FileUploadResponse, FileUploadRequest } from "@/models/file";

const API_URL = `${import.meta.env.VITE_API_URL}/api/v1/files`;

export const fileService = {
    async uploadFile(file: File, category?: string): Promise<FileUploadResponse> {
        try {
            const formData = new FormData();
            formData.append("file", file);
            if (category) {
                formData.append("category", category);
            }

            const response = await axios.post<FileUploadResponse>(
                `${API_URL}/upload`,
                formData,
                {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                }
            );
            return response.data;
        } catch (error) {
            console.error("Error uploading file:", error);
            throw error;
        }
    },

    async uploadMultipleFiles(files: File[], category?: string): Promise<FileUploadResponse[]> {
        try {
            const uploadPromises = files.map(file => this.uploadFile(file, category));
            return await Promise.all(uploadPromises);
        } catch (error) {
            console.error("Error uploading multiple files:", error);
            throw error;
        }
    },

    getFileUrl(fileName: string): string {
        return `${API_URL}/${fileName}`;
    },

    async deleteFile(fileName: string): Promise<void> {
        try {
            await axios.delete(`${API_URL}/${fileName}`);
        } catch (error) {
            console.error("Error deleting file:", error);
            throw error;
        }
    },

    async deleteMultipleFiles(fileNames: string[]): Promise<void> {
        try {
            await axios.delete(`${API_URL}/batch`, {
                data: fileNames
            });
        } catch (error) {
            console.error("Error deleting multiple files:", error);
            throw error;
        }
    },

    async getFileMetadata(fileName: string): Promise<FileUploadResponse> {
        try {
            const response = await axios.get<FileUploadResponse>(`${API_URL}/metadata/${fileName}`);
            return response.data;
        } catch (error) {
            console.error("Error fetching file metadata:", error);
            throw error;
        }
    }
};
