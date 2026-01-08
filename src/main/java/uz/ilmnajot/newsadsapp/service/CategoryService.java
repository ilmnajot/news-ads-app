package uz.ilmnajot.newsadsapp.service;

import uz.ilmnajot.newsadsapp.dto.CategoryDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;

public interface CategoryService {
    ApiResponse addCategory(CategoryDto.AddCategory dto);
    ApiResponse getAllCategories();
    ApiResponse deleteCategory(Long id);
    ApiResponse getCategoryById(Long id);
    ApiResponse getCategoryBySlug(String slug, String lang);
    ApiResponse updateCategory(Long id, CategoryDto.AddCategory dto);
    ApiResponse toggleCategoryStatus(Long id);


    ApiResponse getAllCategoriesByLang(String lang);

    ApiResponse getCategoryByIdAndLang(Long id, String lang);
}
