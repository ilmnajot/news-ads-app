package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.dto.CategoryDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.Category;
import uz.ilmnajot.newsadsapp.entity.CategoryTranslation;
import uz.ilmnajot.newsadsapp.repository.CategoryRepository;
import uz.ilmnajot.newsadsapp.repository.CategoryTranslationRepository;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.service.CategoryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    /**
     * CREATE - Yangi kategoriya yaratish
     */
    @PostMapping("add")
////    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> addCategory(@Valid @RequestBody CategoryDto.AddCategory dto) {
        ApiResponse response = categoryService.addCategory(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * READ - Barcha kategoriyalar (tree structure)
     */
    @GetMapping("/get-all")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getAllCategories() {
        ApiResponse response = categoryService.getAllCategories();
        return ResponseEntity.status(response.getStatus()).body(response);

    }
    /**
     * READ - Barcha kategoriyalar til bo'yicha (tree structure)
     */
    @GetMapping("/get-all-by-lang")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getAllCategoriesByLang(
            @RequestParam(required = false, defaultValue = "uz") String lang) {
        ApiResponse response = categoryService.getAllCategoriesByLang(lang);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Bitta kategoriya (lang bilan)
     */
    @GetMapping("/{id}/lang/{lang}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getCategoryByIdAndLang(
            @PathVariable Long id,
            @PathVariable String lang) {
        ApiResponse response = categoryService.getCategoryByIdAndLang(id, lang);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * READ - Bitta kategoriya (ID bo'yicha)
     */
    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        ApiResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    /**
     * READ - Slug va lang bo'yicha kategoriya
     */
    @GetMapping("/slug/{slug}")
////    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<ApiResponse> getCategoryBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "uz") String lang) {
        ApiResponse response = categoryService.getCategoryBySlug(slug, lang);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    /**
     * UPDATE - Kategoriyani yangilash
     */
    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDto.AddCategory dto) {
        ApiResponse response = categoryService.updateCategory(id, dto);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    /**
     * PATCH - Kategoriya statusini o'zgartirish
     */
    @PatchMapping("/{id}/toggle-status")
//    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Long id) {
        ApiResponse response = categoryService.toggleCategoryStatus(id);
        return ResponseEntity.status(response.getStatus()).body(response);

    }

    /**
     * DELETE - Kategoriyani o'chirish
     */
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        ApiResponse response = categoryService.deleteCategory(id);
        return ResponseEntity.status(response.getStatus()).body(response);

    }
}

