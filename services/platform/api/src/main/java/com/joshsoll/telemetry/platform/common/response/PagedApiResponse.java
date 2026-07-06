package com.joshsoll.telemetry.platform.common.response;

import java.util.List;

public class PagedApiResponse<T> extends ApiResponse<List<T>> {

    private int page;
    private int size;
    private long total;
    private int totalPages;

    public PagedApiResponse(
            List<T> data,
            String message,
            int page,
            int size,
            long total,
            int totalPages) {

        super(data, message);

        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = totalPages;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }

    public int getTotalPages() {
        return totalPages;
    }

}