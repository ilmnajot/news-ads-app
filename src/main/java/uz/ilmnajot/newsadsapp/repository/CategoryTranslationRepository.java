package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.CategoryTranslation;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryTranslationRepository extends JpaRepository<CategoryTranslation, Long> {
    Optional<CategoryTranslation> findByCategoryIdAndLang(Long categoryId, String lang);
    Optional<CategoryTranslation> findBySlugAndLang(String slug, String lang);
//    boolean existsBySlugAndLang(String slug, String lang);
    List<CategoryTranslation> findByLangAndCategoryIsActiveTrue(String lang);
    // Slug kolliziyasini tekshirish
    boolean existsBySlugAndLangAndCategoryIdNot(String slug, String lang, Long categoryId);

    // Slug va lang bo'yicha mavjudligini tekshirish (yangi category uchun)
    boolean existsBySlugAndLang(String slug, String lang);
}

