package com.example.demo.Services;

import com.example.demo.DTOs.RequestDTO.SpecializationRequest;
import com.example.demo.DTOs.ResponseDTO.SpecializationResponse;
import java.util.List;

public interface SpecializationService {

    // Chiều vào nhận Request, chiều ra trả về Response
    SpecializationResponse createSpecialization(SpecializationRequest request);

    SpecializationResponse updateSpecialization(Long id, SpecializationRequest request);

    void deleteSpecialization(Long id);

    List<SpecializationResponse> getAllSpecializations();

    SpecializationResponse getSpecializationById(Long id);
}