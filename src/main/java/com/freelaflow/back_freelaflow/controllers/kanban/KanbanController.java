package com.freelaflow.back_freelaflow.controllers.kanban;

import com.freelaflow.back_freelaflow.controllers.kanban.dto.KanbanRequestDto;
import com.freelaflow.back_freelaflow.controllers.kanban.dto.MoveTaskDto;
import com.freelaflow.back_freelaflow.controllers.kanban.dto.TaskRequestDto;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import com.freelaflow.back_freelaflow.services.KanbanService;
import com.freelaflow.back_freelaflow.services.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/kanbans")
public class KanbanController {

    private final KanbanService kanbanService;
    private final TaskService taskService;
    private final GlobalExceptionHandler globalExceptionHandler;

    public KanbanController(KanbanService kanbanService, TaskService taskService, GlobalExceptionHandler globalExceptionHandler) {
        this.kanbanService = kanbanService;
        this.taskService = taskService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    // --- KANBAN ENDPOINTS ---

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long freelancerId,
            @RequestParam(required = false) Long propostaId,
            @RequestParam(required = false) Boolean ativo) {
        
        Long fId = (freelancerId != null) ? freelancerId : 1L; // Fallback dev
        // Nota: O BFF chama findAllTasks com /kanbans/tasks?... precisamos diferenciar?
        // O BFF diferencia pela URL. Se cair aqui, é kanban.
        return globalExceptionHandler.handleSuccess("Lista de Kanbans", kanbanService.listar(page, limit, fId, ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return globalExceptionHandler.handleSuccess("Kanban encontrado", kanbanService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody KanbanRequestDto dto) {
        return globalExceptionHandler.handleCreateSuccess("Kanban criado", kanbanService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody KanbanRequestDto dto) {
        return globalExceptionHandler.handleSuccess("Kanban atualizado", kanbanService.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        kanbanService.delete(id);
        return globalExceptionHandler.handleSuccess("Kanban removido", null);
    }

    // --- TASK ENDPOINTS ---
    // O BFF usa URLs variadas para Tasks, vamos mapear todas

    // BFF: post<TaskResponseDto>('/kanbans/tasks', createTaskDto);
    @PostMapping("/tasks") 
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody TaskRequestDto dto) {
        return globalExceptionHandler.handleCreateSuccess("Tarefa criada", taskService.create(dto));
    }

    // BFF: get<TaskResponseDto[]>(`/kanbans/${kanbanId}/tasks`);
    @GetMapping("/{kanbanId}/tasks")
    public ResponseEntity<Map<String, Object>> getTasksByKanban(@PathVariable Long kanbanId) {
        return globalExceptionHandler.handleSuccess("Tarefas do Kanban", taskService.listar(kanbanId, null, null));
    }

    // BFF: get<any>(`/kanbans/tasks?kanbanId=...&status=...`)
    @GetMapping("/tasks") 
    public ResponseEntity<Map<String, Object>> getTasksWithFilter(
            @RequestParam Long kanbanId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String prioridade) {
        return globalExceptionHandler.handleSuccess("Tarefas filtradas", taskService.listar(kanbanId, status, prioridade));
    }

    // BFF: get<TaskResponseDto>(`/kanbans/${kanbanId}/tasks/${taskId}`);
    @GetMapping("/{kanbanId}/tasks/{taskId}")
    public ResponseEntity<Map<String, Object>> getTask(@PathVariable Long kanbanId, @PathVariable Long taskId) {
        return globalExceptionHandler.handleSuccess("Tarefa encontrada", taskService.findById(taskId));
    }

    // BFF: put<TaskResponseDto>(`/kanbans/${kanbanId}/tasks/${taskId}`, updateTaskDto);
    @PutMapping("/{kanbanId}/tasks/{taskId}")
    public ResponseEntity<Map<String, Object>> updateTask(@PathVariable Long kanbanId, @PathVariable Long taskId, @RequestBody TaskRequestDto dto) {
        return globalExceptionHandler.handleSuccess("Tarefa atualizada", taskService.update(taskId, dto));
    }

    // BFF: patch<TaskResponseDto>(`/kanbans/${kanbanId}/tasks/${taskId}/move`, moveTaskDto);
    @PatchMapping("/{kanbanId}/tasks/{taskId}/move")
    public ResponseEntity<Map<String, Object>> moveTask(@PathVariable Long kanbanId, @PathVariable Long taskId, @RequestBody MoveTaskDto dto) {
        return globalExceptionHandler.handleSuccess("Tarefa movida", taskService.moveTask(taskId, dto));
    }

    // Endpoint genérico para delete de tasks
    @DeleteMapping("/{kanbanId}/tasks/{taskId}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable Long kanbanId, @PathVariable Long taskId) {
        taskService.delete(taskId);
        return globalExceptionHandler.handleSuccess("Tarefa removida", null);
    }
}