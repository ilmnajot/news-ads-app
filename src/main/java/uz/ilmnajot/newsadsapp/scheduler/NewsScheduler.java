package uz.ilmnajot.newsadsapp.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.entity.News;
import uz.ilmnajot.newsadsapp.enums.NewsStatus;
import uz.ilmnajot.newsadsapp.repository.NewsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsScheduler {

    private final NewsRepository newsRepository;

    @Scheduled(cron = "0 * * * * *") // Every minute
    @Transactional
    public void autoPublishNews() {
        LocalDateTime now = LocalDateTime.now();
        List<News> newsToPublish = newsRepository.findNewsToPublish(NewsStatus.REVIEW, now);
        
        for (News news : newsToPublish) {
            news.setStatus(NewsStatus.PUBLISHED);
            newsRepository.save(news);
            log.info("Auto-published news ID: {}", news.getId());
        }
        
        if (!newsToPublish.isEmpty()) {
            log.info("Auto-published {} news articles", newsToPublish.size());
        }
    }

    @Scheduled(cron = "0 * * * * *") // Every minute
    @Transactional
    public void autoUnpublishNews() {
        LocalDateTime now = LocalDateTime.now();
        List<News> newsToUnpublish = newsRepository.findNewsToUnpublish(NewsStatus.PUBLISHED, now);
        
        for (News news : newsToUnpublish) {
            news.setStatus(NewsStatus.UNPUBLISHED);
            newsRepository.save(news);
            log.info("Auto-unpublished news ID: {}", news.getId());
        }
        
        if (!newsToUnpublish.isEmpty()) {
            log.info("Auto-unpublished {} news articles", newsToUnpublish.size());
        }
    }
}

