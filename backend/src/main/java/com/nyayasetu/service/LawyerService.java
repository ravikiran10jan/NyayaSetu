package com.nyayasetu.service;

import com.nyayasetu.dto.LawyerApplicationRequest;
import com.nyayasetu.model.Lawyer;
import com.nyayasetu.repository.LawyerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class LawyerService {

    private final LawyerRepository lawyerRepository;

    public LawyerService(LawyerRepository lawyerRepository) {
        this.lawyerRepository = lawyerRepository;
    }

    public Lawyer submitApplication(LawyerApplicationRequest request) throws ExecutionException, InterruptedException {
        Lawyer lawyer = new Lawyer();
        lawyer.setName(request.getName());
        lawyer.setEmail(request.getEmail());
        lawyer.setBarCouncil(request.getBarCouncil());
        lawyer.setEnrolmentNumber(request.getEnrolmentNumber());
        lawyer.setJurisdictionState(request.getJurisdictionState());
        lawyer.setCity(request.getCity());
        lawyer.setPracticeAreas(request.getPracticeAreas());
        lawyer.setLanguages(request.getLanguages());
        lawyer.setStatus("pending");
        lawyer.setCreatedAt(System.currentTimeMillis());
        lawyer.setUpdatedAt(System.currentTimeMillis());
        
        return lawyerRepository.save(lawyer);
    }

    public List<Lawyer> getActiveLawyers() throws ExecutionException, InterruptedException {
        return lawyerRepository.findByStatus("active");
    }

    public List<Lawyer> getActiveLawyersByPracticeArea(String practiceArea) throws ExecutionException, InterruptedException {
        return lawyerRepository.findByPracticeAreaAndStatus(practiceArea, "active");
    }

    public List<Lawyer> getActiveLawyersByLanguage(String language) throws ExecutionException, InterruptedException {
        return lawyerRepository.findByLanguageAndStatus(language, "active");
    }

    public Lawyer getLawyerById(String id) throws ExecutionException, InterruptedException {
        return lawyerRepository.findById(id);
    }

    public List<Lawyer> getAllLawyers() throws ExecutionException, InterruptedException {
        return lawyerRepository.findAll();
    }

    public Lawyer updateLawyerStatus(String id, String status, String verificationNote, String reviewedBy) 
            throws ExecutionException, InterruptedException {
        Lawyer lawyer = lawyerRepository.findById(id);
        if (lawyer == null) {
            return null;
        }
        
        lawyer.setStatus(status);
        lawyer.setVerificationNote(verificationNote);
        lawyer.setReviewedBy(reviewedBy);
        lawyer.setReviewedAt(System.currentTimeMillis());
        lawyer.setUpdatedAt(System.currentTimeMillis());
        
        if ("active".equals(status)) {
            lawyer.setVerifiedAt(System.currentTimeMillis());
        }
        
        return lawyerRepository.update(lawyer);
    }
}
