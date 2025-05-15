import axios from 'axios';
import { FileUploadResponse } from '@/models/file';

const API_URL = 'http://localhost:8080/api/files';

export const fileService = {
    async uploadFile(file: File): Promise<FileUploadResponse> {
        const formData = new FormData();
        formData.append('file', file);
        const response = await axios.post<FileUploadResponse>(`${API_URL}/upload`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return response.data;
    },

    getFileUrl(fileName: string): string {
        return `${API_URL}/${fileName}`;
    },

    async deleteFile(fileName: string): Promise<void> {
        await axios.delete(`${API_URL}/${fileName}`);
    }
}; 