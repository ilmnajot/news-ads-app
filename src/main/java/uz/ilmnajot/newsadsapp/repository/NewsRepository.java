package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.News;
import uz.ilmnajot.newsadsapp.enums.NewsStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {

    @Query("SELECT n FROM News n WHERE n.isDeleted = false")
    Page<News> findAllNonDeleted(Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.isDeleted = false AND n.author.id = :authorId")
    Page<News> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.isDeleted = false AND n.category.id = :categoryId")
    Page<News> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.isDeleted = false AND n.status = :status")
    Page<News> findByStatus(@Param("status") NewsStatus status, Pageable pageable);

    @Query("SELECT n FROM News n JOIN n.tags t WHERE n.isDeleted = false AND t.code = :tagCode")
    Page<News> findByTagCode(@Param("tagCode") String tagCode, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.isDeleted = false AND n.status = :status " +
            "AND (n.publishAt IS NULL OR n.publishAt <= :now) " +
            "AND (n.unpublishAt IS NULL OR n.unpublishAt > :now)")
    Page<News> findPublishedNews(@Param("status") NewsStatus status,
                                 @Param("now") LocalDateTime now,
                                 Pageable pageable);

    @Query("SELECT n FROM News n JOIN n.translations t WHERE n.isDeleted = false " +
            "AND n.status = :status AND t.lang = :lang " +
            "AND (n.publishAt IS NULL OR n.publishAt <= :now) " +
            "AND (n.unpublishAt IS NULL OR n.unpublishAt > :now)")
    Page<News> findPublishedNewsByLang(@Param("status") NewsStatus status,
                                       @Param("lang") String lang,
                                       @Param("now") LocalDateTime now,
                                       Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.status = :status " +
            "AND n.publishAt IS NOT NULL AND n.publishAt <= :now and n.isDeleted=false")
    List<News> findNewsToPublish(@Param("status") NewsStatus status,
                                 @Param("now") LocalDateTime now);

    @Query("SELECT n FROM News n WHERE n.status = :status " +
            "AND n.unpublishAt IS NOT NULL AND n.unpublishAt <= :now and n.isDeleted=false")
    List<News> findNewsToUnpublish(@Param("status") NewsStatus status,
                                   @Param("now") LocalDateTime now);

    Optional<News> findByIdAndIsDeletedFalse(Long id);

    /**
     * PUBLIC: Get news list with filters
     * Problem: Pagination + JOIN FETCH ishlamaydi!
     * Solution: 2-step query
     */

    // Step 1: Get IDs with pagination
    @Query("""
            SELECT DISTINCT n.id
            FROM News n
            LEFT JOIN n.translations nt
            LEFT JOIN n.tags tag
            WHERE n.status = 'PUBLISHED'
            AND n.isDeleted = false
            AND (n.publishAt IS NULL OR n.publishAt <= :now)
            AND (n.unpublishAt IS NULL OR n.unpublishAt > :now)
            AND (:lang IS NULL OR nt.lang = :lang)
            AND (:categoryId IS NULL OR n.category.id = :categoryId)
            AND (:tag IS NULL OR tag.code = :tag) order by n.id, n.publishAt desc
            """)
    Page<Long> findPublicNewsIds(
            @Param("lang") String lang,
            @Param("categoryId") Long categoryId,
            @Param("tag") String tag,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );


    // Step 2: Fetch news by IDs with JOIN FETCH
    @Query("SELECT DISTINCT n FROM News n " +
            "LEFT JOIN FETCH n.translations " +
            "LEFT JOIN FETCH n.tags " +
            "LEFT JOIN FETCH n.category " +
            "LEFT JOIN FETCH n.coverMedia " +
            "WHERE n.id IN :ids " +
            "ORDER BY n.publishAt DESC")
    List<News> findNewsByIds(@Param("ids") List<Long> ids);

    /**
     * PUBLIC: Get single news by slug
     */
    @Query("SELECT n FROM News n " +
            "JOIN n.translations nt " +
            "WHERE nt.slug = :slug " +
            "AND nt.lang = :lang " +
            "AND n.status = 'PUBLISHED' " +
            "AND n.isDeleted = false " +
            "AND (n.publishAt IS NULL OR n.publishAt <= :now) " +
            "AND (n.unpublishAt IS NULL OR n.unpublishAt > :now)")
    Optional<News> findPublicNewsBySlug(
            @Param("slug") String slug,
            @Param("lang") String lang,
            @Param("now") LocalDateTime now
    );
}

