package com.nyayasetu.controller;

import com.nyayasetu.dto.ApiResponse;
import com.nyayasetu.dto.LawyerApplicationRequest;
import com.nyayasetu.model.Lawyer;
import com.nyayasetu.service.LawyerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class AdvocateController {

    private final LawyerService lawyerService;

    public AdvocateController(LawyerService lawyerService) {
        this.lawyerService = lawyerService;
    }

    @GetMapping("/advocates")
    public ResponseEntity<ApiResponse<List<Lawyer>>> getActiveLawyers(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String search) throws ExecutionException, InterruptedException {
        
        List<Lawyer> lawyers;
        
        if (area != null && !area.isEmpty()) {
            lawyers = lawyerService.getActiveLawyersByPracticeArea(area);
        } else if (language != null && !language.isEmpty()) {
            lawyers = lawyerService.getActiveLawyersByLanguage(language);
        } else {
            lawyers = lawyerService.getActiveLawyers();
        }
        
        // TODO: Implement search filtering if needed
        
        return ResponseEntity.ok(ApiResponse.success(lawyers));
    }

    @GetMapping("/advocates/{id}")
    public ResponseEntity<ApiResponse<Lawyer>> getLawyerById(@PathVariable String id) 
            throws ExecutionException, InterruptedException {
        
        Lawyer lawyer = lawyerService.getLawyerById(id);
        if (lawyer == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Lawyer not found"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(lawyer));
    }

    @PostMapping("/lawyer-applications")
    public ResponseEntity<ApiResponse<Lawyer>> submitLawyerApplication(
            @RequestBody LawyerApplicationRequest request) throws ExecutionException, InterruptedException {
        
        Lawyer lawyer = lawyerService.submitApplication(request);
        return ResponseEntity.status(201).body(ApiResponse.success("Application submitted successfully", lawyer));
    }
}
