package uz.ilmnajot.newsadsapp.controller.admin;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.dto.CategoryDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.service.CategoryService;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@Hidden
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * CREATE - Yangi kategoriya yaratish
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse addCategory(@Valid @RequestBody CategoryDto.AddCategory dto) {
        return categoryService.addCategory(dto);
    }

    /**
     * READ - Barcha kategoriyalar
     */
    @GetMapping("/get-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getAllCategories() {
        return categoryService.getAllCategories();
    }

    /**
     * READ - Barcha kategoriyalar til bo'yicha
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    @GetMapping("/get-all-by-lang")
    public ApiResponse getAllCategoriesByLang(
            @RequestParam(required = false, defaultValue = "uz") String lang) {
        return categoryService.getAllCategoriesByLang(lang);
    }

    /**
     * Bitta kategoriya (lang bilan)
     */
    @GetMapping("/{id}/lang/{lang}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getCategoryByIdAndLang(
            @PathVariable Long id,
            @PathVariable String lang) {
        return categoryService.getCategoryByIdAndLang(id, lang);
    }

    /**
     * READ - Bitta kategoriya (ID bo'yicha)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);

    }

    /**
     * READ - Slug va lang bo'yicha kategoriya
     */
    @GetMapping("/slug/{slug}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ApiResponse getCategoryBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "uz") String lang) {
        return categoryService.getCategoryBySlug(slug, lang);

    }

    /**
     * UPDATE - Kategoriyani yangilash
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDto.AddCategory dto) {
        return categoryService.updateCategory(id, dto);

    }

    /**
     * PATCH - Kategoriya statusini o'zgartirish
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse toggleStatus(@PathVariable Long id) {
        return categoryService.toggleCategoryStatus(id);

    }

    /**
     * DELETE - Kategoriyani o'chirish
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}

