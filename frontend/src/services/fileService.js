import axios from 'axios';

const API_URL = 'http://localhost:8080/api/files';

export const fileService = {
    async uploadFile(file) {
        const formData = new FormData();
        formData.append('file', file);
        const response = await axios.post(`${API_URL}/upload`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return response.data;
    },

    getFileUrl(fileName) {
        return `${API_URL}/${fileName}`;
    },

    async deleteFile(fileName) {
        await axios.delete(`${API_URL}/${fileName}`);
    }
}; 