package com.kush.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> {
    private java.util.List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
