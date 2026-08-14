export interface Column<T> {
    field: keyof T,
    header: string;
}
