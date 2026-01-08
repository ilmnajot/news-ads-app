package uz.ilmnajot.newsadsapp.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.dto.TagDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.service.TagService;

@RestController
@RequestMapping("/api/v1/admin/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;


    @GetMapping
    public ResponseEntity<ApiResponse> getAllTags(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        ApiResponse apiResponse = this.tagService.getAllTags(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);

    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public HttpEntity<ApiResponse> addTag(@RequestBody TagDto.AddTag dto) {
        ApiResponse apiResponse = this.tagService.addTag(dto);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> updateTag(@PathVariable Long id,
                                                 @RequestBody TagDto.UpdateTag dto) {
        ApiResponse apiResponse = this.tagService.updateTag(id, dto);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HttpEntity<?> inActivateTag(@PathVariable Long id) {
        ApiResponse apiResponse = this.tagService.deleteTag(id);
        return ResponseEntity.status(apiResponse.getStatus()).build();
    }
    @GetMapping("/{id}")
    public HttpEntity<ApiResponse> getTagById(@PathVariable Long id) {
        ApiResponse apiResponse = this.tagService.getTagById(id);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

}

