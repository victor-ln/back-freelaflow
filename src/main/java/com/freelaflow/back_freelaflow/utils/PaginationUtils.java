package com.freelaflow.back_freelaflow.utils;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaginationUtils {
    public static <T, R> PaginatedResponseDto<R> toPaginatedResponse(Page<T> page, Function<T, R> mapper) {
        List<R> data = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        PaginatedResponseDto.MetaDto meta = PaginatedResponseDto.MetaDto.builder()
                .totalItems(page.getTotalElements())
                .itemCount(data.size())
                .itemsPerPage(page.getSize())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber() + 1)
                .build();

        return PaginatedResponseDto.<R>builder()
                .data(data)
                .meta(meta)
                .build();
    }
}
