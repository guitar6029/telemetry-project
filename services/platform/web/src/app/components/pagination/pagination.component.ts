import { Component, computed, input, output } from "@angular/core";
import { DEFAULT_PAGE_SIZE_OPTIONS } from "./constants/pagination.constants";
import { PaginationItem } from "./types/pagination.types";
import { LabelComponent } from "../label/label.component";

const MAX_VISIBLE_PAGES = 5;

@Component({
    selector: 'telemetry-pagination',
    templateUrl: './pagination.component.html',
    imports: [LabelComponent]
})
export class PaginationComponent {

    page = input.required<number>();
    size = input.required<number>();
    total = input.required<number>();
    totalPages = input.required<number>();

    readonly PAGE_SIZE_OPTIONS = DEFAULT_PAGE_SIZE_OPTIONS;

    pageChange = output<number>();
    sizeChange = output<number>();

    protected hasPrevious = computed(() => this.page() > 0);

    protected hasNext = computed(
        () => this.page() < this.totalPages() - 1
    );

    protected startItem = computed(
        () => this.total() === 0
            ? 0
            : this.page() * this.size() + 1
    );

    protected endItem = computed(() =>
        Math.min(
            (this.page() + 1) * this.size(),
            this.total()
        )
    );

    protected visiblePages = computed<PaginationItem[]>(() => {
        const totalPages = this.totalPages();
        const currentPage = this.page();

        if (totalPages <= MAX_VISIBLE_PAGES) {
            return Array.from(
                { length: totalPages },
                (_, index) => index
            );
        }

        const lastPage = totalPages - 1;

        // Beginning
        if (currentPage <= 2) {
            return [
                0,
                1,
                2,
                3,
                4,
                'ellipsis',
                lastPage
            ];
        }

        // End
        if (currentPage >= lastPage - 2) {
            return [
                0,
                'ellipsis',
                lastPage - 4,
                lastPage - 3,
                lastPage - 2,
                lastPage - 1,
                lastPage
            ];
        }

        // Middle
        return [
            0,
            'ellipsis',
            currentPage - 1,
            currentPage,
            currentPage + 1,
            'ellipsis',
            lastPage
        ];
    });

    changePageSize(event: Event): void {
        const select = event.target as HTMLSelectElement;
        const size = Number(select.value);

        this.sizeChange.emit(size);
    }
}
