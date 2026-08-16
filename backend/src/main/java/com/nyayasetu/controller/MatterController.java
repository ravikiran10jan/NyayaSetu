package com.nyayasetu.controller;

import com.nyayasetu.config.FirebaseUserPrincipal;
import com.nyayasetu.dto.ApiResponse;
import com.nyayasetu.dto.MatterRequest;
import com.nyayasetu.dto.MatterResponse;
import com.nyayasetu.model.LegalMatter;
import com.nyayasetu.model.MatterRecord;
import com.nyayasetu.service.MatterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matters")
public class MatterController {

    private final MatterService matterService;

    public MatterController(MatterService matterService) {
        this.matterService = matterService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MatterResponse>> createMatter(
            @RequestBody MatterRequest request,
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        LegalMatter matter = matterService.createMatter(request, principal.getEmail(), principal.getUid());
        return ResponseEntity.status(201).body(
            ApiResponse.success("Matter created successfully", new MatterResponse(matter)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<MatterResponse>>> getMyMatters(
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        List<LegalMatter> matters = matterService.getClientMatters(principal.getUid());
        List<MatterResponse> responses = matters.stream()
            .map(MatterResponse::new)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MatterResponse>> getMatterById(
            @PathVariable String id,
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        LegalMatter matter = matterService.getMatterById(id);
        if (matter == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("Matter not found"));
        }
        
        // Check if user is owner or assigned lawyer
        if (!matter.getOwnerUserId().equals(principal.getUid()) && 
            !matter.getLawyerId().equals(principal.getUid())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Not authorized to view this matter"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(new MatterResponse(matter)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MatterResponse>> updateMatter(
            @PathVariable String id,
            @RequestBody MatterRequest request,
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        try {
            LegalMatter matter = matterService.updateMatter(id, request, principal.getUid());
            if (matter == null) {
                return ResponseEntity.status(404).body(ApiResponse.error("Matter not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("Matter updated successfully", new MatterResponse(matter)));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/records")
    public ResponseEntity<ApiResponse<MatterRecord>> addMatterRecord(
            @PathVariable String id,
            @RequestBody MatterRecord record,
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        try {
            // Check if user is owner or assigned lawyer
            if (!matterService.isMatterOwnerOrLawyer(id, principal.getUid())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Not authorized to add records to this matter"));
            }
            
            MatterRecord savedRecord = matterService.addMatterRecord(id, record, principal.getEmail());
            return ResponseEntity.status(201).body(
                ApiResponse.success("Record added successfully", savedRecord));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/records")
    public ResponseEntity<ApiResponse<List<MatterRecord>>> getMatterRecords(
            @PathVariable String id,
            @AuthenticationPrincipal FirebaseUserPrincipal principal) throws ExecutionException, InterruptedException {
        
        // Check if user is owner or assigned lawyer
        if (!matterService.isMatterOwnerOrLawyer(id, principal.getUid())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Not authorized to view records for this matter"));
        }
        
        List<MatterRecord> records = matterService.getMatterRecords(id);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
