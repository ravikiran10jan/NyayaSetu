package com.nyayasetu.controller;

import com.nyayasetu.config.FirebaseUserPrincipal;
import com.nyayasetu.dto.ApiResponse;
import com.nyayasetu.model.Grievance;
import com.nyayasetu.model.Lawyer;
import com.nyayasetu.service.GrievanceService;
import com.nyayasetu.service.LawyerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final LawyerService lawyerService;
    private final GrievanceService grievanceService;

    public AdminController(LawyerService lawyerService, GrievanceService grievanceService) {
        this.lawyerService = lawyerService;
        this.grievanceService = grievanceService;
    }

    @GetMapping("/lawyers")
    public ResponseEntity<ApiResponse<List<Lawyer>>> getAllLawyers() throws ExecutionException, InterruptedException {
        List<Lawyer> lawyers = lawyerService.getAllLawyers();
        return ResponseEntity.ok(ApiResponse.success(lawyers));
    }

    @PutMapping("/lawyers/{id}/verify")
    public ResponseEntity<ApiResponse<Lawyer>> updateLawyerStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        String status = body.get("status");
        String verificationNote = body.get("verificationNote");
        
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Status is required"));
        }
        
        if (!"active".equals(status) && !"rejected".equals(status) && !"suspended".equals(status)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status. Must be active, rejected, or suspended"));
        }
        
        Lawyer lawyer = lawyerService.updateLawyerStatus(id, status, verificationNote, principal.getEmail());
        if (lawyer == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Lawyer not found"));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Lawyer status updated successfully", lawyer));
    }

    @GetMapping("/grievances")
    public ResponseEntity<ApiResponse<List<Grievance>>> getAllGrievances() throws ExecutionException, InterruptedException {
        List<Grievance> grievances = grievanceService.getAllGrievances();
        return ResponseEntity.ok(ApiResponse.success(grievances));
    }
}
