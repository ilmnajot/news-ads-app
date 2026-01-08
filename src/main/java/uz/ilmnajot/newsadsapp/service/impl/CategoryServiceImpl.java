package uz.ilmnajot.newsadsapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.dto.CategoryDto;
import uz.ilmnajot.newsadsapp.dto.CategoryTranslationDto;
import uz.ilmnajot.newsadsapp.dto.CategoryWithLangDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.Category;
import uz.ilmnajot.newsadsapp.entity.CategoryTranslation;
import uz.ilmnajot.newsadsapp.exception.CategorySlugExistsException;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.mapper.CategoryMapper;
import uz.ilmnajot.newsadsapp.repository.CategoryRepository;
import uz.ilmnajot.newsadsapp.repository.CategoryTranslationRepository;
import uz.ilmnajot.newsadsapp.service.CategoryService;
import uz.ilmnajot.newsadsapp.util.SlugGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryTranslationRepository categoryTranslationRepository;
    private final SlugGenerator slugGenerator;
    private final CategoryMapper categoryMapper;

    /**
     * CREATE - Yangi kategoriya yaratish
     */
    @Override
    @Transactional
    public ApiResponse addCategory(CategoryDto.AddCategory dto) {

        // Validatsiya: 3 ta til majburiy
        if (dto.getTranslationsDtoList() == null || dto.getTranslationsDtoList().size() != 3) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("3 ta til majburiy: uz, ru, en")
                    .build();
        }

        // Parent kategoriyani tekshirish (agar berilgan bo'lsa)
        Category parent = null;
        if (dto.getParentId() != null) {
            parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found: " + dto.getParentId()));
        }

        // Category yaratish
        Category category = Category.builder()
                .parent(parent)
                .isActive(true)
                .build();

        // Translations yaratish
        List<CategoryTranslation> translations = new ArrayList<>();

        for (CategoryTranslationDto.AddCategoryTranslation translationDto : dto.getTranslationsDtoList()) {

            // Slug generatsiya
            String slug = slugGenerator.generateUniqueSlug(
                    translationDto.getTitle(),
                    translationDto.getLang(),
                    null  // yangi category uchun categoryId yo'q
            );

            // Slug kolliziyasini tekshirish
            if (categoryTranslationRepository.existsBySlugAndLang(slug, translationDto.getLang())) {
                throw new CategorySlugExistsException(
                        "Slug already exists: " + slug + " for lang: " + translationDto.getLang());
            }

            CategoryTranslation translation = CategoryTranslation.builder()
                    .category(category)
                    .lang(translationDto.getLang())
                    .title(translationDto.getTitle())
                    .slug(slug)
                    .description(translationDto.getDescription())
                    .build();

            translations.add(translation);
        }

        category.setTranslations(translations);

        // Saqlash (cascade=ALL tufayli translations ham saqlanadi)
        Category savedCategory = categoryRepository.save(category);

        // Response DTO'ga mapping
        CategoryDto responseDto = this.categoryMapper.toDto(savedCategory);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully added category")
                .data(responseDto)
                .build();
    }

    /**
     * READ - Barcha kategoriyalar (hierarchical tree)
     */
    @Transactional(readOnly = true)
    public ApiResponse getAllCategories() {

        // Root kategoriyalarni olish
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        // Tree yaratish
        List<CategoryDto> categoryTree = rootCategories
                .stream()
                .map(this.categoryMapper::toDto)
                .collect(Collectors.toList());

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(categoryTree)
                .build();
    }

    /**
     * Barcha kategoriyalarni olish (hierarchical tree, faqat 1 ta tilda)
     */
    @Transactional(readOnly = true)
    public ApiResponse getAllCategoriesByLang(String lang) {

        // Validate lang
        if (!lang.matches("uz|ru|en")) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Invalid language. Use: uz, ru, en")
                    .build();
        }

        // Root kategoriyalarni olish
        List<Category> rootCategories = categoryRepository.findRootCategoriesWithTranslations();

        // DTO'ga mapping (recursive - children bilan)
        List<CategoryWithLangDto> categoryTree = rootCategories.stream()
                .map(category -> mapToDtoWithLang(category, lang))
                .filter(dto -> dto != null) // translation bo'lmagan kategoriyalarni olib tashlash
                .collect(Collectors.toList());

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(categoryTree)
                .build();
    }

    /**
     * Bitta kategoriyani olish (lang bilan)
     */
    @Transactional(readOnly = true)
    public ApiResponse getCategoryByIdAndLang(Long id, String lang) {

        Category category = categoryRepository.findByIdWithTranslations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        CategoryWithLangDto dto = mapToDtoWithLang(category, lang);

        if (dto == null) {
            return ApiResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Translation not found for language: " + lang)
                    .build();
        }

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(dto)
                .build();
    }

    /**
     * Helper: Category → CategoryWithLangDto (recursive, children bilan)
     */
    private CategoryWithLangDto mapToDtoWithLang(Category category, String lang) {

        // Current lang uchun translation topish
        CategoryTranslation translation = category.getTranslations().stream()
                .filter(t -> t.getLang().equals(lang))
                .findFirst()
                .orElse(null);

        // Agar translation bo'lmasa, null qaytarish
        if (translation == null) {
            return null;
        }

        // DTO yaratish
        CategoryWithLangDto dto = CategoryWithLangDto.builder()
                .id(category.getId())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .isActive(category.getIsActive())
                .lang(lang)
                .title(translation.getTitle())
                .slug(translation.getSlug())
                .description(translation.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();

        // Children'ni ham mapping qilish (recursive)
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            List<CategoryWithLangDto> childrenDtos = category.getChildren().stream()
                    .map(child -> mapToDtoWithLang(child, lang))
                    .filter(childDto -> childDto != null)
                    .collect(Collectors.toList());
            dto.setChildren(childrenDtos);
        }

        return dto;
    }


    /**
     * READ - Bitta kategoriyani olish (ID bo'yicha)
     */
    @Transactional(readOnly = true)
    public ApiResponse getCategoryById(Long id) {

        Category category = categoryRepository.findByIdWithTranslations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        CategoryDto dto = this.categoryMapper.toDto(category);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(dto)
                .build();
    }

    /**
     * READ - Slug va lang bo'yicha kategoriya
     */
    @Transactional(readOnly = true)
    public ApiResponse getCategoryBySlug(String slug, String lang) {

        Category category = categoryRepository.findBySlugAndLang(slug, lang)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug + " and lang: " + lang));

        CategoryDto dto = this.categoryMapper.toDto(category);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(dto)
                .build();
    }

    /**
     * UPDATE - Kategoriyani yangilash
     */
    @Transactional
    public ApiResponse updateCategory(Long id, CategoryDto.AddCategory dto) {

        // Mavjud kategoriyani topish
        Category category = categoryRepository.findByIdWithTranslations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Parent'ni yangilash (agar berilgan bo'lsa)
        if (dto.getParentId() != null) {
            // O'z-o'zini parent qilmaslik
            if (dto.getParentId().equals(id)) {
                return ApiResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Error")
                        .build();
            }

            Category newParent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found: " + dto.getParentId()));

            category.setParent(newParent);
        } else {
            category.setParent(null);
        }

        // Translations yangilash
        if (dto.getTranslationsDtoList() != null && !dto.getTranslationsDtoList().isEmpty()) {

            // Eski translations'ni o'chirish
            category.getTranslations().clear();

            // Yangi translations qo'shish
            for (CategoryTranslationDto.AddCategoryTranslation translationDto : dto.getTranslationsDtoList()) {

                // Slug generatsiya
                String slug = slugGenerator.generateUniqueSlug(
                        translationDto.getTitle(),
                        translationDto.getLang(),
                        id  // hozirgi category uchun
                );

                CategoryTranslation translation = CategoryTranslation.builder()
                        .category(category)
                        .lang(translationDto.getLang())
                        .title(translationDto.getTitle())
                        .slug(slug)
                        .description(translationDto.getDescription())
                        .build();

                category.getTranslations().add(translation);
            }
        }

        category.setUpdatedAt(LocalDateTime.now());

        // Saqlash
        Category updatedCategory = categoryRepository.save(category);

        CategoryDto responseDto = this.categoryMapper.toDto(updatedCategory);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(responseDto)
                .build();
    }

    /**
     * UPDATE - Kategoriya statusini o'zgartirish (activate/deactivate)
     */
    @Transactional
    public ApiResponse toggleCategoryStatus(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setIsActive(!category.getIsActive());
        category.setUpdatedAt(LocalDateTime.now());

        categoryRepository.save(category);

        String status = category.getIsActive() ? "activated" : "deactivated";
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message(status)
                .build();
    }

    /**
     * DELETE - Kategoriyani o'chirish (soft delete)
     */
    @Transactional
    public ApiResponse deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Children borligini tekshirish
        List<Category> children = categoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            return ApiResponse.builder()
                    .status(HttpStatus.OK)
                    .message("Cannot delete category with children. Delete children first.")
                    .build();
        }

        // O'chirish
        categoryRepository.delete(category);

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .build();
    }

}
