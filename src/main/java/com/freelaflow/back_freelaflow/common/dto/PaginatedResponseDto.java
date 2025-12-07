package com.freelaflow.back_freelaflow.common.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponseDto<T> {
    private List<T> data;
    private MetaDto meta;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MetaDto {
        private Long totalItems;
        private Integer itemCount;
        private Integer itemsPerPage;
        private Integer totalPages;
        private Integer currentPage;
    }
}
