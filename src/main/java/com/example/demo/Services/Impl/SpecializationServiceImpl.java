package com.example.demo.Services.Impl;

import com.example.demo.DTOs.RequestDTO.SpecializationRequest;
import com.example.demo.DTOs.ResponseDTO.SpecializationResponse;
import com.example.demo.Entities.Specialization;
import com.example.demo.Repositories.SpecializationRepository;
import com.example.demo.Services.SpecializationService;
import com.example.demo.Exceptions.ResourceNotFoundException;
import com.example.demo.Exceptions.BadRequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Ghi log hệ thống bổ trợ cho việc Audit sau này
public class SpecializationServiceImpl implements SpecializationService {

    private final SpecializationRepository specializationRepository;

    @Override
    @Transactional
    @CacheEvict(value = "specializations", allEntries = true)
    // Chiều vào nhận RequestDTO, chiều ra trả về ResponseDTO
    public SpecializationResponse createSpecialization(SpecializationRequest request) {
        log.info("Đang tạo chuyên khoa mới: {}", request.getName());

        // Kiểm tra trùng tên chuyên khoa
        if (specializationRepository.existsByName(request.getName())) {
            throw new BadRequestException("Chuyên khoa '" + request.getName() + "' đã tồn tại trong hệ thống.");
        }

        Specialization specialization = new Specialization();
        specialization.setName(request.getName());
        specialization.setDescription(request.getDescription());

        Specialization savedSpec = specializationRepository.save(specialization);
        return convertToResponse(savedSpec);
    }

    @Override
    @Transactional
    @CacheEvict(value = "specializations", allEntries = true)
    public SpecializationResponse updateSpecialization(Long id, SpecializationRequest request) {
        log.info("Đang cập nhật chuyên khoa có ID: {}", id);

        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyên khoa với ID: " + id));

        // Kiểm tra trùng tên với các chuyên khoa khác (trừ chính nó)
        if (specializationRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BadRequestException("Tên chuyên khoa '" + request.getName() + "' đã được sử dụng.");
        }

        specialization.setName(request.getName());
        specialization.setDescription(request.getDescription());

        Specialization updatedSpec = specializationRepository.save(specialization);
        return convertToResponse(updatedSpec);
    }

    @Override
    @Transactional
    @CacheEvict(value = "specializations", allEntries = true)
    public void deleteSpecialization(Long id) {
        log.warn("Đang tiến hành xóa chuyên khoa có ID: {}", id);

        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyên khoa với ID: " + id));

        // Kiểm tra ràng buộc thực tế trước khi xóa
        if (specialization.getDoctors() != null && !specialization.getDoctors().isEmpty()) {
            throw new BadRequestException("Không thể xóa chuyên khoa này vì đang có bác sĩ đang làm việc.");
        }

        specializationRepository.delete(specialization);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "specializations")
    // Tận dụng Redis Cache để tăng tốc độ phản hồi cho danh sách hiển thị ngoài trang chủ
    public List<SpecializationResponse> getAllSpecializations() {
        log.info("Lấy danh sách chuyên khoa từ Database (Do chưa được Cache hoặc Cache đã bị dọn dẹp)...");

        List<Specialization> specializations = specializationRepository.findAll();
        return specializations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecializationResponse getSpecializationById(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyên khoa với ID: " + id));
        return convertToResponse(specialization);
    }

    // Helper Method: Chuyển đổi từ Entity an toàn sang dữ liệu hiển thị ResponseDTO công khai
    private SpecializationResponse convertToResponse(Specialization spec) {
        SpecializationResponse res = new SpecializationResponse();
        // Cập nhật theo đúng chuẩn tên trường ID trong Entity mới của bạn (specializationId)
        res.setSpecializationId(spec.getSpecializationId());
        res.setName(spec.getName());
        res.setDescription(spec.getDescription());
        return res;
    }
}