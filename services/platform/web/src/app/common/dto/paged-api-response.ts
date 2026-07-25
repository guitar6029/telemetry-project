import { ApiResponse } from "./api-response";

export interface PagedApiResponse<T> extends ApiResponse<T[]> {
    message: string | null;
    page: number;
    size: number;
    total: number;
    totalPages: number;
}
