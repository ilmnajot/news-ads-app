package uz.ilmnajot.newsadsapp.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.entity.News;
import uz.ilmnajot.newsadsapp.entity.NewsHistory;
import uz.ilmnajot.newsadsapp.enums.NewsStatus;
import uz.ilmnajot.newsadsapp.repository.NewsHistoryRepository;
import uz.ilmnajot.newsadsapp.repository.NewsRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsScheduler {

    private final NewsRepository newsRepository;
    private final NewsHistoryRepository newsHistoryRepository;

    /**
     * Auto-publish news at scheduled time
     * Runs every minute
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void autoPublishNews() {
        LocalDateTime now = LocalDateTime.now();

        // Find news ready to publish
        List<News> newsToPublish = newsRepository.findNewsToPublish(NewsStatus.REVIEW,now);

        if (newsToPublish.isEmpty()) {
            return;  // Hech narsa yo'q
        }

        log.info("Auto-publishing {} news articles", newsToPublish.size());

        for (News news : newsToPublish) {
            NewsStatus oldStatus = news.getStatus();

            // Change status
            news.setStatus(NewsStatus.PUBLISHED);

            // Create history record
            Map<String, Object> diff = new HashMap<>();
            diff.put("action", "auto_publish");
            diff.put("field", "status");
            diff.put("oldValue", oldStatus.name());
            diff.put("newValue", NewsStatus.PUBLISHED.name());
            diff.put("timestamp", now.toString());
            diff.put("scheduledAt", news.getPublishAt().toString());
            diff.put("reason", "Scheduled publish time reached");

            NewsHistory history = NewsHistory.builder()
                    .news(news)
                    .changedBy(news.getAuthor())  // System user yoki author
                    .fromStatus(oldStatus.name())
                    .toStatus(NewsStatus.PUBLISHED.name())
                    .diffJson(diff)
                    .build();

            newsHistoryRepository.save(history);

            log.info("Auto-published: newsId={}, oldStatus={}, publishAt={}",
                    news.getId(), oldStatus, news.getPublishAt());
        }

        // Batch save (1 ta query)
        newsRepository.saveAll(newsToPublish);

        log.info("Successfully auto-published {} news articles", newsToPublish.size());
    }

    /**
     * Auto-unpublish news at scheduled time
     * Runs every minute
     */
    @Scheduled(cron = "0 * * * * *")  // Har daqiqada
    @Transactional
    public void autoUnpublishNews() {
        LocalDateTime now = LocalDateTime.now();

        // Find news ready to unpublish
        List<News> newsToUnpublish = newsRepository.findNewsToUnpublish(NewsStatus.PUBLISHED,now);

        if (newsToUnpublish.isEmpty()) {
            return;
        }

        log.info("Auto-unpublishing {} news articles", newsToUnpublish.size());

        for (News news : newsToUnpublish) {
            NewsStatus oldStatus = news.getStatus();

            // Change status
            news.setStatus(NewsStatus.UNPUBLISHED);

            // Create history record
            Map<String, Object> diff = new HashMap<>();
            diff.put("action", "auto_unpublish");
            diff.put("field", "status");
            diff.put("oldValue", oldStatus.name());
            diff.put("newValue", NewsStatus.UNPUBLISHED.name());
            diff.put("timestamp", now.toString());
            diff.put("scheduledAt", news.getUnpublishAt().toString());
            diff.put("reason", "Scheduled unpublish time reached");

            NewsHistory history = NewsHistory.builder()
                    .news(news)
                    .changedBy(news.getAuthor())
                    .fromStatus(oldStatus.name())
                    .toStatus(NewsStatus.UNPUBLISHED.name())
                    .diffJson(diff)
                    .build();

            newsHistoryRepository.save(history);

            log.info("Auto-unpublished: newsId={}, oldStatus={}, unpublishAt={}",
                    news.getId(), oldStatus, news.getUnpublishAt());
        }

        // Batch save
        newsRepository.saveAll(newsToUnpublish);

        log.info("Successfully auto-unpublished {} news articles", newsToUnpublish.size());
    }
}