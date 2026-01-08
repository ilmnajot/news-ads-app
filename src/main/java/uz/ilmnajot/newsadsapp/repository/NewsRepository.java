package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.News;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    @Query("SELECT n FROM News n WHERE n.isDeleted = false")
    Page<News> findAllNonDeleted(Pageable pageable);
    
    @Query("SELECT n FROM News n WHERE n.isDeleted = false AND n.author.id = :authorId")
    Page<News> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);
    
    @Query("SELECT n FROM News n WHERE n.isDeleted = false AND n.category.id = :categoryId")
    Page<News> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT n FROM News n WHERE n.isDeleted = false AND n.status = :status")
    Page<News> findByStatus(@Param("status") News.Status status, Pageable pageable);
    
    @Query("SELECT n FROM News n JOIN n.tags t WHERE n.isDeleted = false AND t.code = :tagCode")
    Page<News> findByTagCode(@Param("tagCode") String tagCode, Pageable pageable);
    
    @Query("SELECT n FROM News n WHERE n.isDeleted = false AND n.status = :status " +
           "AND (n.publishAt IS NULL OR n.publishAt <= :now) " +
           "AND (n.unpublishAt IS NULL OR n.unpublishAt > :now)")
    Page<News> findPublishedNews(@Param("status") News.Status status, 
                                  @Param("now") LocalDateTime now, 
                                  Pageable pageable);
    
    @Query("SELECT n FROM News n JOIN n.translations t WHERE n.isDeleted = false " +
           "AND n.status = :status AND t.lang = :lang " +
           "AND (n.publishAt IS NULL OR n.publishAt <= :now) " +
           "AND (n.unpublishAt IS NULL OR n.unpublishAt > :now)")
    Page<News> findPublishedNewsByLang(@Param("status") News.Status status,
                                       @Param("lang") String lang,
                                       @Param("now") LocalDateTime now,
                                       Pageable pageable);
    
    @Query("SELECT n FROM News n WHERE n.status = :status " +
           "AND n.publishAt IS NOT NULL AND n.publishAt <= :now")
    List<News> findNewsToPublish(@Param("status") News.Status status, 
                                  @Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM News n WHERE n.status = :status " +
           "AND n.unpublishAt IS NOT NULL AND n.unpublishAt <= :now")
    List<News> findNewsToUnpublish(@Param("status") News.Status status, 
                                    @Param("now") LocalDateTime now);
    
    Optional<News> findByIdAndIsDeletedFalse(Long id);
}

