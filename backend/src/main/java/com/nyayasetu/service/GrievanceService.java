package com.nyayasetu.service;

import com.nyayasetu.model.Grievance;
import com.nyayasetu.repository.GrievanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class GrievanceService {

    private final GrievanceRepository grievanceRepository;

    public GrievanceService(GrievanceRepository grievanceRepository) {
        this.grievanceRepository = grievanceRepository;
    }

    public Grievance submitGrievance(String reporterEmail, String reporterUserId, 
                                     String matterId, String category, String details) 
            throws ExecutionException, InterruptedException {
        
        Grievance grievance = new Grievance();
        grievance.setReporterEmail(reporterEmail);
        grievance.setReporterUserId(reporterUserId);
        grievance.setMatterId(matterId);
        grievance.setCategory(category);
        grievance.setDetails(details);
        grievance.setStatus("open");
        grievance.setCreatedAt(System.currentTimeMillis());
        grievance.setUpdatedAt(System.currentTimeMillis());

        return grievanceRepository.save(grievance);
    }

    public List<Grievance> getAllGrievances() throws ExecutionException, InterruptedException {
        return grievanceRepository.findAll();
    }

    public Grievance getGrievanceById(String id) throws ExecutionException, InterruptedException {
        return grievanceRepository.findById(id);
    }
}
