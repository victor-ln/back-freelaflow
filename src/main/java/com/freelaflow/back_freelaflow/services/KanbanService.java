package com.freelaflow.back_freelaflow.services;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class KanbanService {

    // Método necessário para o Controller compilar
    public Map<String, Object> getKanbans(int page, int limit, Boolean active, Long proposalId) {
        // Lógica provisória apenas para compilar e retornar vazio
        Map<String, Object> response = new HashMap<>();
        response.put("data", "Implementação pendente");
        return response;
    }
}