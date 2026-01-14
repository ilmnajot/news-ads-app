package uz.ilmnajot.newsadsapp.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    /**
     * GET ALL TAGS WITH PAGINATION
     *
     */
    @GetMapping("/get-all")
    public ApiResponse getAllTags(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return this.tagService.getAllTags(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse addTag(@RequestBody TagDto.AddTag dto) {
        return this.tagService.addTag(dto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse updateTag(
            @PathVariable Long id,
            @RequestBody TagDto.UpdateTag dto) {
        return this.tagService.updateTag(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse inActivateTag(@PathVariable Long id) {
        return this.tagService.deleteTag(id);
    }

    @GetMapping("/{id}")
    public ApiResponse getTagById(@PathVariable Long id) {
        return this.tagService.getTagById(id);
    }

}

