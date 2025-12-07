package com.freelaflow.back_freelaflow.controllers.dashboard;

import com.freelaflow.back_freelaflow.controllers.dashboard.dto.DashboardResponseDto;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import com.freelaflow.back_freelaflow.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    private final GlobalExceptionHandler globalExceptionHandler;

    public DashboardController(DashboardService dashboardService, GlobalExceptionHandler globalExceptionHandler) {
        this.dashboardService = dashboardService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @GetMapping("/{freelancerId}")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable Long freelancerId) {
        DashboardResponseDto stats = dashboardService.getStats(freelancerId);
        return globalExceptionHandler.handleSuccess("Dados do dashboard recuperados", stats);
    }
}