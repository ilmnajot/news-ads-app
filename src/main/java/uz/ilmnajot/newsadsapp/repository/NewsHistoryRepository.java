package uz.ilmnajot.newsadsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ilmnajot.newsadsapp.entity.NewsHistory;

import java.util.List;

@Repository
public interface NewsHistoryRepository extends JpaRepository<NewsHistory, Long> {
    List<NewsHistory> findByNewsIdOrderByChangedAtDesc(Long newsId);
}

