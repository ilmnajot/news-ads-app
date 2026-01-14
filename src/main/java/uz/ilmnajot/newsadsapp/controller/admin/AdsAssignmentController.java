package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse createAssignment(@Valid @RequestBody AdsAssignmentDto.CreateAssignment request) {
        return assignmentService.createAssignment(request);
    }

    /**
     * GET All Assignments
     * GET /admin/ads/assignments
     */
    @GetMapping("/get-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getAllAssignments() {
        return assignmentService.getAllAssignments();
    }

    /**
     * GET Assignment by ID
     * GET /admin/ads/assignments/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getAssignmentById(@PathVariable Long id) {
        return assignmentService.getAssignmentById(id);
    }

    /**
     * UPDATE Assignment (PUT)
     * PUT /admin/ads/assignments/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AdsAssignmentDto.UpdateAssignment request) {
        return assignmentService.updateAssignment(id, request);
    }

    /**
     * DELETE Assignment
     * DELETE /admin/ads/assignments/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse deleteAssignment(@PathVariable Long id) {
        return assignmentService.deleteAssignment(id);
    }
}