package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.NewsHistory;

import java.util.List;

@Repository
public interface NewsHistoryRepository extends JpaRepository<NewsHistory, Long> {
    @Query("SELECT h FROM NewsHistory h WHERE h.news.id = :newsId ORDER BY h.createdAt DESC")
    List<NewsHistory> findByNewsIdOrderByCreatedAtDesc(@Param("newsId") Long newsId);
}

