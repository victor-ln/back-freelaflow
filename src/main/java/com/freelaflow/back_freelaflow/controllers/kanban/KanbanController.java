package com.freelaflow.back_freelaflow.controllers.kanban;

import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/kanban")
public class KanbanController {
    private final GlobalExceptionHandler globalExceptionHandler;

    public KanbanController(GlobalExceptionHandler globalExceptionHandler) {
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getKanbans(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long proposalId) {

        Map<String, Object> resultado = kanbanService.getKanbans(page, limit, active, proposalId);
        return globalExceptionHandler.handleCreateSuccess("Kanbans listados com sucesso", resultado);
    }
}
