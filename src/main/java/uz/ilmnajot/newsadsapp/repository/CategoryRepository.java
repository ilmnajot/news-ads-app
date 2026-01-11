package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Root kategoriyalar (parent_id = NULL)
    List<Category> findByParentIsNull();

    // Parent bo'yicha children topish
    List<Category> findByParentId(Long parentId);

    // ID bilan category va translations
    @Query("SELECT DISTINCT c FROM Category c " +
            "LEFT JOIN FETCH c.translations " +
            "WHERE c.id = :id")
    Optional<Category> findByIdWithTranslations(@Param("id") Long id);

    // Barcha kategoriyalar (translations bilan)
    @Query("SELECT DISTINCT c FROM Category c " +
            "LEFT JOIN FETCH c.translations " +
            "ORDER BY c.id")
    List<Category> findAllWithTranslations();

    // Slug va lang bo'yicha
    @Query("SELECT c FROM Category c " +
            "JOIN c.translations t " +
            "WHERE t.slug = :slug AND t.lang = :lang")
    Optional<Category> findBySlugAndLang(@Param("slug") String slug, @Param("lang") String lang);

    @Query("select c from Category as c join CategoryTranslation as ct on ct.category.id=c.id and ct.lang=:lang")
    List<Category> getAllCategoriesByLang(@Param(value = "lang") String lang);

    /**
     * Root kategoriyalarni olish (translations bilan)
     */
    @Query("SELECT DISTINCT c FROM Category c " +
            "LEFT JOIN FETCH c.translations " +
            "WHERE c.parent IS NULL " +
            "ORDER BY c.id")
    List<Category> findRootCategoriesWithTranslations();

    @Query("""
    select distinct c
    from Category c
    join c.translations ct
    where c.isActive = true
      and ct.lang = :lang
""")
    List<Category> findPublicCategories(@Param("lang") String lang);

}

