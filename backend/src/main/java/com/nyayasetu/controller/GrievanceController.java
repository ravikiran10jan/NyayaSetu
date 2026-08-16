package com.nyayasetu.controller;

import com.nyayasetu.config.FirebaseUserPrincipal;
import com.nyayasetu.dto.ApiResponse;
import com.nyayasetu.model.Grievance;
import com.nyayasetu.service.GrievanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/grievances")
public class GrievanceController {

    private final GrievanceService grievanceService;

    public GrievanceController(GrievanceService grievanceService) {
        this.grievanceService = grievanceService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Grievance>> submitGrievance(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        String matterId = body.get("matterId");
        String category = body.get("category");
        String details = body.get("details");
        
        if (category == null || category.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Category is required"));
        }
        if (details == null || details.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Details are required"));
        }
        
        Grievance grievance = grievanceService.submitGrievance(
            principal.getEmail(), principal.getUid(), matterId, category, details);
        
        return ResponseEntity.status(201).body(
            ApiResponse.success("Grievance submitted successfully", grievance));
    }
}
