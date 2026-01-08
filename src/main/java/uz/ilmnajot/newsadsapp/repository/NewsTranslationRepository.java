package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.NewsTranslation;

import java.util.Optional;

@Repository
public interface NewsTranslationRepository extends JpaRepository<NewsTranslation, Long> {
    Optional<NewsTranslation> findByNewsIdAndLang(Long newsId, String lang);
    Optional<NewsTranslation> findBySlugAndLang(String slug, String lang);
    boolean existsBySlugAndLang(String slug, String lang);
    
    @Query("SELECT nt FROM NewsTranslation nt JOIN nt.news n " +
           "WHERE nt.slug = :slug AND nt.lang = :lang " +
           "AND n.status = 'PUBLISHED' AND n.isDeleted = false " +
           "AND (n.publishAt IS NULL OR n.publishAt <= CURRENT_TIMESTAMP) " +
           "AND (n.unpublishAt IS NULL OR n.unpublishAt > CURRENT_TIMESTAMP)")
    Optional<NewsTranslation> findPublishedBySlugAndLang(@Param("slug") String slug, 
                                                          @Param("lang") String lang);
}

