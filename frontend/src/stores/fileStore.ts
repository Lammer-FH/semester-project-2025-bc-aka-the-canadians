import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { fileService } from '@/services';
import { FileUploadResponse } from '@/models/file';

export const useFileStore = defineStore('file', () => {
    // State
    const uploadedFiles = ref<FileUploadResponse[]>([]);
    const loading = ref<boolean>(false);
    const error = ref<string | null>(null);

    // Getters
    const getUploadedFiles = computed(() => uploadedFiles.value);
    const isLoading = computed(() => loading.value);
    const getError = computed(() => error.value);

    // Actions
    const uploadFile = async (file: File) => {
        try {
            loading.value = true;
            const response = await fileService.uploadFile(file);
            uploadedFiles.value.push(response);
            return response;
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const getFileUrl = (fileName: string): string => {
        return fileService.getFileUrl(fileName);
    };

    const deleteFile = async (fileName: string) => {
        try {
            loading.value = true;
            await fileService.deleteFile(fileName);
            uploadedFiles.value = uploadedFiles.value.filter(file => file.fileName !== fileName);
        } catch (err) {
            error.value = err instanceof Error ? err.message : 'An error occurred';
            throw err;
        } finally {
            loading.value = false;
        }
    };

    return {
        // State
        uploadedFiles,
        loading,
        error,
        // Getters
        getUploadedFiles,
        isLoading,
        getError,
        // Actions
        uploadFile,
        getFileUrl,
        deleteFile
    };
}); 