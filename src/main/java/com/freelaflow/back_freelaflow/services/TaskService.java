package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.controllers.kanban.dto.MoveTaskDto;
import com.freelaflow.back_freelaflow.controllers.kanban.dto.TaskRequestDto;
import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import com.freelaflow.back_freelaflow.models.Freelancer;
import com.freelaflow.back_freelaflow.models.Kanban;
import com.freelaflow.back_freelaflow.models.Task;
import com.freelaflow.back_freelaflow.repository.FreelancerRepository;
import com.freelaflow.back_freelaflow.repository.KanbanRepository;
import com.freelaflow.back_freelaflow.repository.TaskRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final KanbanRepository kanbanRepository;
    private final FreelancerRepository freelancerRepository;

    public TaskService(TaskRepository taskRepository, KanbanRepository kanbanRepository, FreelancerRepository freelancerRepository) {
        this.taskRepository = taskRepository;
        this.kanbanRepository = kanbanRepository;
        this.freelancerRepository = freelancerRepository;
    }

    public List<Task> listar(Long kanbanId, String status, String prioridade) {
        Specification<Task> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (kanbanId != null) predicates.add(cb.equal(root.get("kanban").get("id"), kanbanId));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (prioridade != null) predicates.add(cb.equal(root.get("prioridade"), prioridade));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return taskRepository.findAll(spec);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
    }

    @Transactional
    public Task create(TaskRequestDto dto) {
        Kanban kanban = kanbanRepository.findById(dto.getKanbanId())
                .orElseThrow(() -> new ResourceNotFoundException("Kanban não encontrado"));
        Freelancer freelancer = freelancerRepository.findById(dto.getFreelancerId())
                .orElseThrow(() -> new ResourceNotFoundException("Freelancer não encontrado"));

        Task task = new Task();
        task.setTitulo(dto.getTitulo());
        task.setDescricao(dto.getDescricao());
        task.setStatus(dto.getStatus() != null ? dto.getStatus() : "A Fazer");
        task.setPrioridade(dto.getPrioridade() != null ? dto.getPrioridade() : "Média");
        task.setKanban(kanban);
        task.setFreelancer(freelancer);

        return taskRepository.save(task);
    }

    @Transactional
    public Task update(Long id, TaskRequestDto dto) {
        Task task = findById(id);
        if (dto.getTitulo() != null) task.setTitulo(dto.getTitulo());
        if (dto.getDescricao() != null) task.setDescricao(dto.getDescricao());
        if (dto.getStatus() != null) task.setStatus(dto.getStatus());
        if (dto.getPrioridade() != null) task.setPrioridade(dto.getPrioridade());
        return taskRepository.save(task);
    }

    @Transactional
    public Task moveTask(Long id, MoveTaskDto dto) {
        Task task = findById(id);
        task.setStatus(dto.getNovoStatus());
        // Lógica de reordenação poderia entrar aqui se houver campo 'ordem'
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) throw new ResourceNotFoundException("Tarefa não encontrada");
        taskRepository.deleteById(id);
    }
}