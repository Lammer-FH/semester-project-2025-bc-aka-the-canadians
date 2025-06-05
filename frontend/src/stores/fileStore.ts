import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { fileService } from "@/services";
import { FileUploadResponse } from "@/models/file";

export const useFileStore = defineStore("file", () => {
    const uploadedFiles = ref<FileUploadResponse[]>([]);
    const loading = ref<boolean>(false);
    const error = ref<string | null>(null);

    const getUploadedFiles = computed(() => uploadedFiles.value);
    const isLoading = computed(() => loading.value);
    const getError = computed(() => error.value);

    const uploadFile = async (file: File) => {
        try {
            loading.value = true;
            const response = await fileService.uploadFile(file);
            uploadedFiles.value.push(response);
            return response;
        } catch (err) {
            error.value =
                err instanceof Error ? err.message : "An error occurred";
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
            uploadedFiles.value = uploadedFiles.value.filter(
                (file) => file.fileName !== fileName,
            );
        } catch (err) {
            error.value =
                err instanceof Error ? err.message : "An error occurred";
            throw err;
        } finally {
            loading.value = false;
        }
    };

    return {
        uploadedFiles,
        loading,
        error,
        getUploadedFiles,
        isLoading,
        getError,
        uploadFile,
        getFileUrl,
        deleteFile,
    };
});
