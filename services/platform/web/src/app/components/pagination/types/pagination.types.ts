export type PaginationItem = number | 'ellipsis';


export interface PaginationState {
    page: number;
    size: number;
    total: number;
    totalPages: number;
}
