package com.nyayasetu.service;

import com.nyayasetu.dto.MatterRequest;
import com.nyayasetu.model.LegalMatter;
import com.nyayasetu.model.MatterRecord;
import com.nyayasetu.repository.MatterRepository;
import com.nyayasetu.repository.MatterRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class MatterService {

    private final MatterRepository matterRepository;
    private final MatterRecordRepository matterRecordRepository;

    public MatterService(MatterRepository matterRepository, MatterRecordRepository matterRecordRepository) {
        this.matterRepository = matterRepository;
        this.matterRecordRepository = matterRecordRepository;
    }

    public LegalMatter createMatter(MatterRequest request, String ownerEmail, String ownerUserId) 
            throws ExecutionException, InterruptedException {
        
        LegalMatter matter = new LegalMatter();
        matter.setOwnerEmail(ownerEmail);
        matter.setOwnerUserId(ownerUserId);
        matter.setTitle(request.getTitle());
        matter.setPracticeArea(request.getPracticeArea());
        matter.setState(request.getState());
        matter.setLanguage(request.getLanguage());
        matter.setUrgency(request.getUrgency() != null ? request.getUrgency() : "standard");
        matter.setOpposingParty(request.getOpposingParty());
        matter.setLawyerId(request.getLawyerId());
        matter.setStatus("intake");
        matter.setConflictStatus("pending");
        matter.setCreatedAt(System.currentTimeMillis());
        matter.setUpdatedAt(System.currentTimeMillis());

        return matterRepository.save(matter);
    }

    public List<LegalMatter> getClientMatters(String ownerUserId) throws ExecutionException, InterruptedException {
        return matterRepository.findByOwnerUserId(ownerUserId);
    }

    public LegalMatter getMatterById(String id) throws ExecutionException, InterruptedException {
        return matterRepository.findById(id);
    }

    public LegalMatter updateMatter(String id, MatterRequest request, String ownerUserId) 
            throws ExecutionException, InterruptedException {
        
        LegalMatter matter = matterRepository.findById(id);
        if (matter == null) {
            return null;
        }
        
        // Verify ownership
        if (!matter.getOwnerUserId().equals(ownerUserId)) {
            throw new SecurityException("Not authorized to update this matter");
        }

        if (request.getTitle() != null) matter.setTitle(request.getTitle());
        if (request.getPracticeArea() != null) matter.setPracticeArea(request.getPracticeArea());
        if (request.getState() != null) matter.setState(request.getState());
        if (request.getLanguage() != null) matter.setLanguage(request.getLanguage());
        if (request.getUrgency() != null) matter.setUrgency(request.getUrgency());
        if (request.getOpposingParty() != null) matter.setOpposingParty(request.getOpposingParty());
        if (request.getLawyerId() != null) matter.setLawyerId(request.getLawyerId());
        
        matter.setUpdatedAt(System.currentTimeMillis());

        return matterRepository.update(matter);
    }

    public MatterRecord addMatterRecord(String matterId, MatterRecord record, String author) 
            throws ExecutionException, InterruptedException {
        
        LegalMatter matter = matterRepository.findById(matterId);
        if (matter == null) {
            throw new IllegalArgumentException("Matter not found");
        }

        record.setMatterId(matterId);
        record.setAuthor(author);
        record.setCreatedAt(System.currentTimeMillis());

        return matterRecordRepository.save(record);
    }

    public List<MatterRecord> getMatterRecords(String matterId) throws ExecutionException, InterruptedException {
        return matterRecordRepository.findByMatterId(matterId);
    }

    public boolean isMatterOwnerOrLawyer(String matterId, String userId) throws ExecutionException, InterruptedException {
        LegalMatter matter = matterRepository.findById(matterId);
        if (matter == null) {
            return false;
        }
        return matter.getOwnerUserId().equals(userId) || matter.getLawyerId().equals(userId);
    }
}
