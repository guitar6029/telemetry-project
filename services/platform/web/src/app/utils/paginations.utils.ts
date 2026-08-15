import { PagedApiResponse } from "../common/dto/paged-api-response.dto";

export function getPaginationState<T>(response: PagedApiResponse<T>) {
    return {
        page: response.page,
        size: response.size,
        total: response.total,
        totalPages: response.totalPages
    };
}
