package uz.ilmnajot.newsadsapp.service;

import uz.ilmnajot.newsadsapp.dto.AdsAssignmentDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;

public interface AdsAssignmentService {
    ApiResponse createAssignment(AdsAssignmentDto.CreateAssignment request);
    ApiResponse getAllAssignments();
    ApiResponse getAssignmentById(Long id);
    ApiResponse updateAssignment(Long id, AdsAssignmentDto.UpdateAssignment request);
    ApiResponse deleteAssignment(Long id);
    ApiResponse findActiveAssignmentsByPlacement(String code, String lang, Long categoryId);
}
