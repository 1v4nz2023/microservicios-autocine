package com.grupo4.autocine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin panel APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard data", description = "Returns data for the admin dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Data retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<?> getDashboardData() {
        Map<String, Object> dashboardData = new HashMap<>();
        
        // Aquí puedes añadir datos relevantes para el dashboard de administrador
        // Por ejemplo, estadísticas, conteos, etc.
        dashboardData.put("totalUsers", 100);
        dashboardData.put("totalMovies", 50);
        dashboardData.put("activeBookings", 25);
        dashboardData.put("revenue", 5000);
        
        return ResponseEntity.ok(dashboardData);
    }
} 