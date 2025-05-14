import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { fileService } from '@/services';

export const useFileStore = defineStore('file', () => {
    // State
    const uploadedFiles = ref([]);
    const loading = ref(false);
    const error = ref(null);

    // Getters
    const getUploadedFiles = computed(() => uploadedFiles.value);
    const isLoading = computed(() => loading.value);
    const getError = computed(() => error.value);

    // Actions
    const uploadFile = async (file) => {
        // TODO: Implement API call
    };

    const getFileUrl = (fileName) => {
        // TODO: Implement URL generation
    };

    const deleteFile = async (fileName) => {
        // TODO: Implement API call
    };

    return {
        // State
        uploadedFiles,
        error,
        // Getters
        getUploadedFiles,
        getError,
        // Actions
        uploadFile,
        getFileUrl,
        deleteFile
    };
}); 