package com.freelaflow.back_freelaflow.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationDto {
    private Integer page;
    private Integer limit;
    private String search;

    public Integer getPage() {
        return page != null ? page : 1;
    }

    public Integer getLimit() {
        return limit != null ? limit : 10;
    }
}
