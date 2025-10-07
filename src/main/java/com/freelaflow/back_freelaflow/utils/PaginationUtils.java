package com.freelaflow.back_freelaflow.utils;

import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaginationUtils {
    public static <T, R> Map<String, Object> toPaginatedResponse(Page<T> page, Function<T, R> mapper) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("data", page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList()));
        resposta.put("total", page.getTotalElements());
        resposta.put("page", page.getNumber() + 1);
        resposta.put("limit", page.getSize());
        resposta.put("totalPages", page.getTotalPages());
        return resposta;
    }
}
