export interface FileUploadResponse {
    fileName: string;
    fileUrl: string;
    fileSize: number;
    mimeType: string;
}

export interface FileUploadRequest {
    file: File;
    category?: string;
}

export interface FileDeleteRequest {
    fileName: string;
}
