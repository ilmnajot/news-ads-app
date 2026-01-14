package uz.ilmnajot.newsadsapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.dto.NewsCreateRequest;
import uz.ilmnajot.newsadsapp.dto.NewsHistoryDto;
import uz.ilmnajot.newsadsapp.dto.NewsResponse;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.*;
import uz.ilmnajot.newsadsapp.enums.NewsStatus;
import uz.ilmnajot.newsadsapp.exception.BadRequestException;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.filter.NewsFilter;
import uz.ilmnajot.newsadsapp.mapper.NewsHistoryMapper;
import uz.ilmnajot.newsadsapp.mapper.NewsMapper;
import uz.ilmnajot.newsadsapp.repository.*;
import uz.ilmnajot.newsadsapp.util.HtmlSanitizer;
import uz.ilmnajot.newsadsapp.util.SlugGenerator;
import uz.ilmnajot.newsadsapp.util.UserUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsTranslationRepository newsTranslationRepository;
    private final NewsHistoryRepository newsHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final SlugGenerator slugGenerator;
    private final UserUtil userUtil;
    private final NewsMapper newsMapper;
    private final NewsHistoryMapper newsHistoryMapper;

    //done
    @Transactional
    public NewsResponse createNews(NewsCreateRequest request) {
        User currentUser = this.userUtil.getCurrentUser();
        News news = News.builder()
                .author(currentUser)
                .status(NewsStatus.valueOf(request.getStatus().toUpperCase()))
                .isFeatured(request.getIsFeatured() != null && request.getIsFeatured())
                .publishAt(request.getPublishAt())
                .unpublishAt(request.getUnpublishAt())
                .isDeleted(false)
                .build();

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            news.setCategory(category);
        }

        if (request.getCoverMediaId() != null) {
            Media media = mediaRepository.findById(request.getCoverMediaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Media not found"));
            news.setCoverMedia(media);
        }

        news = newsRepository.save(news);

        // Create translations
        List<NewsTranslation> translations = new ArrayList<>();
        for (Map.Entry<String, NewsCreateRequest.NewsTranslationRequest> entry : request.getTranslations().entrySet()) {
            String lang = entry.getKey();
            NewsCreateRequest.NewsTranslationRequest tr = entry.getValue();

            String slug = tr.getSlug();
            if (slug == null || slug.isEmpty()) {
                slug = slugGenerator.generateSlug(tr.getTitle());
            }
            NewsTranslation translation = NewsTranslation.builder()
                    .news(news)
                    .lang(lang)
                    .title(tr.getTitle())
                    .slug(slug)
                    .summary(tr.getSummary())
                    .content(HtmlSanitizer.sanitize(tr.getContent()))
                    .metaTitle(tr.getMetaTitle())
                    .metaDescription(tr.getMetaDescription())
                    .build();

            translations.add(newsTranslationRepository.save(translation));
        }
        news.setTranslations(translations);

        // Add tags
        if (request.getTagCodes() != null && !request.getTagCodes().isEmpty()) {
            Set<Tag> tags = request.getTagCodes().stream()
                    .map(code -> tagRepository.findByCode(code)
                            .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + code)))
                    .collect(Collectors.toSet());
            news.setTags(tags);
        }
        // Record history
        recordStatusChange(news, news.getStatus().name(), currentUser);
        return this.newsMapper.toDto(newsRepository.save(news));
    }

    public ApiResponse getNews(Pageable pageable, NewsFilter filter) {
        Page<News> page = this.newsRepository.findAll(filter, pageable);
        List<NewsResponse> list = this.newsMapper.toDto(page.getContent());
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(list)
                .pages(page.getTotalPages())
                .elements(page.getTotalElements())
                .build();
    }

    public NewsResponse getNewsById(Long id) {
        News news = newsRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));
        return this.newsMapper.toDto(news);
    }

    @Transactional
    public NewsResponse updateNewsStatus(Long id, NewsStatus newStatus) {
        News news = newsRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));
        //old one!
        NewsStatus oldStatus = news.getStatus();
        if (newStatus.equals(oldStatus)) {
            throw new BadRequestException("The status has not been changed yet!");
        }
        Map<String, Object> diff = Map.of(
                "field", "status",
                "from", oldStatus,
                "to", newStatus
        );
        User currentUser = getCurrentUser();
        recordStatusChange(news, oldStatus, newStatus, currentUser, diff);
        news.setStatus(newStatus);
        news = newsRepository.save(news);
        return this.newsMapper.toDto(news);
    }

    private void recordStatusChange(
            News news,
            NewsStatus fromStatus,
            NewsStatus toStatus,
            User user,
            Map<String, Object> diff) {
        NewsHistory history = NewsHistory.builder()
                .news(news)
                .changedBy(user)
                .fromStatus(fromStatus.name())
                .toStatus(toStatus.name())
                .diffJson(diff)
                .build();
        newsHistoryRepository.save(history);
    }

    @Transactional
    public void softDeleteNews(Long id) {
        News news = newsRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));

        User currentUser = getCurrentUser();

        // History yozish
        Map<String, Object> diff = Map.of(
                "action", "soft_delete",
                "oldDeletedStatus", false,
                "newDeletedStatus", true,
                "timestamp", LocalDateTime.now().toString(),
                "user", currentUser.getUsername()
        );

        recordNewsHistory(news, currentUser, "ACTIVE", "DELETED", diff);

        // Delete
        news.setIsDeleted(true);
        news.setDeletedAt(LocalDateTime.now());
        newsRepository.save(news);
    }

    @Transactional
    public void restoreNews(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));
        if (!news.getIsDeleted()) {
            throw new IllegalArgumentException("News is not deleted");
        }
        User user = this.getCurrentUser();

        Boolean oldDeletedStatus = news.getIsDeleted();
        news.setIsDeleted(false);
        news.setDeletedAt(null);
        News news2 = newsRepository.save(news);
        Map<String, Object> diff = Map.of(
                "action", "restore",
                "oldDeletedStatus", oldDeletedStatus,     // true
                "newDeletedStatus", false,                 // false
                "user", user.getUsername(),
                "timestamp", LocalDateTime.now().toString()
        );
        this.recordNewsHistory(news, user, news.getIsDeleted().toString(), news2.getIsDeleted().toString(), diff);
    }

    private void recordNewsHistory(News news, User user, String from, String to, Map<String, Object> diff) {
        NewsHistory history = NewsHistory.builder()
                .news(news)           // ← qo'shildi!
                .changedBy(user)
                .fromStatus(from)
                .toStatus(to)
                .diffJson(diff)
                .build();
        newsHistoryRepository.save(history);
    }

    @Transactional
    public ApiResponse hardDeleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new ResourceNotFoundException("News not found");
        }
        try {
            newsRepository.deleteById(id);
            return ApiResponse.builder()
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed to delete news")
                    .build();
        }
    }

    public ApiResponse getNewsHistory(Long newsId) {
        List<NewsHistory> historyList = newsHistoryRepository.findByNewsIdOrderByCreatedAtDesc(newsId);
        List<NewsHistoryDto> dtoList = this.newsHistoryMapper.toDto(historyList);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(dtoList)
                .build();

    }

    private void recordStatusChange(News news, String toStatus, User user) {
        NewsHistory history = NewsHistory.builder()
                .news(news)
                .changedBy(user)
                .fromStatus(null)
                .toStatus(toStatus)
                .build();
        newsHistoryRepository.save(history);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}

