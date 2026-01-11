package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.dto.AdsAssignmentDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.service.AdsAssignmentService;

@RestController
@RequestMapping("/api/v1/admin/ads/assignments")
@RequiredArgsConstructor
public class AdsAssignmentController {

    private final AdsAssignmentService assignmentService;

    /**
     * CREATE Assignment
     * POST /admin/ads/assignments
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> createAssignment(
            @Valid @RequestBody AdsAssignmentDto.CreateAssignment request) {
        ApiResponse response = assignmentService.createAssignment(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * GET All Assignments
     * GET /admin/ads/assignments
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getAllAssignments() {
        
        ApiResponse response = assignmentService.getAllAssignments();
        return ResponseEntity.ok(response);
    }

    /**
     * GET Assignment by ID
     * GET /admin/ads/assignments/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getAssignmentById(@PathVariable Long id) {
        
        ApiResponse response = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE Assignment (PUT)
     * PUT /admin/ads/assignments/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AdsAssignmentDto.UpdateAssignment request) {
        
        ApiResponse response = assignmentService.updateAssignment(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE Assignment
     * DELETE /admin/ads/assignments/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteAssignment(@PathVariable Long id) {
        
        ApiResponse response = assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(response);
    }
}