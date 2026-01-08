package uz.ilmnajot.newsadsapp.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.entity.Tag;
import uz.ilmnajot.newsadsapp.repository.TagRepository;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagRepository tagRepository;

    @GetMapping
    public ResponseEntity<Page<Tag>> getAllTags(Pageable pageable) {
        return ResponseEntity.ok(tagRepository.findAll(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Tag> createTag(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Tag code is required");
        }
        
        if (tagRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Tag with code already exists: " + code);
        }
        
        Tag tag = Tag.builder()
                .code(code)
                .isActive(true)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(tagRepository.save(tag));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Tag> updateTag(@PathVariable Long id, 
                                          @RequestBody Map<String, Object> updates) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        
        if (updates.containsKey("isActive")) {
            tag.setIsActive(Boolean.parseBoolean(updates.get("isActive").toString()));
        }
        
        return ResponseEntity.ok(tagRepository.save(tag));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag not found");
        }
        tagRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

