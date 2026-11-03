package com.campus.trade.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用分页结果。JSP 中可直接使用 ${pageData.items}、${pageData.pages} 等属性。
 */
public class PageResult<T> {

    private final List<T> items;
    private final int page;
    private final int size;
    private final long total;
    private final int pages;
    private final String url;

    private PageResult(List<T> items, int page, int size, long total, String url) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
        this.pages = total == 0 ? 0 : (int) ((total + size - 1) / size);
        this.url = url;
    }

    public static <T> PageResult<T> of(List<T> items, int page, int size, long total, String url) {
        return new PageResult<>(items, page, size, total, url);
    }

    public static <T> PageResult<T> empty(int page, int size, String url) {
        return new PageResult<>(List.of(), page, size, 0, url);
    }

    public List<T> getItems() {
        return items;
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

    public int getPages() {
        return pages;
    }

    public String getUrl() {
        return url;
    }

    public boolean isHasPrev() {
        return page > 1;
    }

    public boolean isHasNext() {
        return page < pages;
    }

    public long getStartRow() {
        return total == 0 ? 0 : (long) (page - 1) * size + 1;
    }

    public long getEndRow() {
        return Math.min((long) page * size, total);
    }

    /** 只展示首页、末页和当前页附近，避免页数过多时撑破布局。 */
    public List<Integer> getPageNumbers() {
        List<Integer> numbers = new ArrayList<>();
        if (pages <= 0) {
            return numbers;
        }
        for (int i = 1; i <= pages; i++) {
            if (i == 1 || i == pages || Math.abs(i - page) <= 2) {
                numbers.add(i);
            }
        }
        return numbers;
    }
}
