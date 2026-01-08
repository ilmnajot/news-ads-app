package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.entity.Category;
import uz.ilmnajot.newsadsapp.entity.CategoryTranslation;
import uz.ilmnajot.newsadsapp.repository.CategoryRepository;
import uz.ilmnajot.newsadsapp.repository.CategoryTranslationRepository;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryTranslationRepository categoryTranslationRepository;

    @GetMapping
    public ResponseEntity<Page<Category>> getAllCategories(Pageable pageable) {
        return ResponseEntity.ok(categoryRepository.findAll(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Map<String, Object> request) {
        Category category = Category.builder()
                .isActive(true)
                .build();
        
        if (request.containsKey("parentId")) {
            Long parentId = Long.parseLong(request.get("parentId").toString());
            category.setParent(categoryRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found")));
        }
        
        category = categoryRepository.save(category);
        
        // Create translations
        if (request.containsKey("translations")) {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, String>> translations = (Map<String, Map<String, String>>) request.get("translations");
            for (Map.Entry<String, Map<String, String>> entry : translations.entrySet()) {
                String lang = entry.getKey();
                Map<String, String> tr = entry.getValue();
                
                CategoryTranslation translation = CategoryTranslation.builder()
                        .category(category)
                        .lang(lang)
                        .title(tr.get("title"))
                        .slug(tr.get("slug"))
                        .description(tr.get("description"))
                        .build();
                categoryTranslationRepository.save(translation);
            }
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return ResponseEntity.ok(category);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, 
                                                    @RequestBody Map<String, Object> updates) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        if (updates.containsKey("isActive")) {
            category.setIsActive(Boolean.parseBoolean(updates.get("isActive").toString()));
        }
        
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

