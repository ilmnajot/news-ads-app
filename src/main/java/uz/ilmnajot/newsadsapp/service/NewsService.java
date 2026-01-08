package uz.ilmnajot.newsadsapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.dto.request.NewsCreateRequest;
import uz.ilmnajot.newsadsapp.dto.response.NewsResponse;
import uz.ilmnajot.newsadsapp.entity.*;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.repository.*;
import uz.ilmnajot.newsadsapp.util.HtmlSanitizer;
import uz.ilmnajot.newsadsapp.util.SlugGenerator;

import java.time.LocalDateTime;
import java.util.*;
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

    @Transactional
    public NewsResponse createNews(NewsCreateRequest request) {
        User currentUser = getCurrentUser();
        News news = News.builder()
                .author(currentUser)
                .status(News.Status.valueOf(request.getStatus().toUpperCase()))
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
                slug = SlugGenerator.generate(tr.getTitle());
            }

            // Check uniqueness and generate unique slug
            slug = SlugGenerator.generateUnique(slug, s -> 
                newsTranslationRepository.existsBySlugAndLang(s, lang));

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
        recordStatusChange(news, null, news.getStatus().name(), currentUser);

        return mapToResponse(newsRepository.save(news));
    }

    public Page<NewsResponse> getNews(Pageable pageable, String status, Long authorId, 
                                     Long categoryId, String tagCode, String lang) {
        Page<News> newsPage;
        
        if (status != null) {
            newsPage = newsRepository.findByStatus(News.Status.valueOf(status.toUpperCase()), pageable);
        } else if (authorId != null) {
            newsPage = newsRepository.findByAuthorId(authorId, pageable);
        } else if (categoryId != null) {
            newsPage = newsRepository.findByCategoryId(categoryId, pageable);
        } else if (tagCode != null) {
            newsPage = newsRepository.findByTagCode(tagCode, pageable);
        } else {
            newsPage = newsRepository.findAllNonDeleted(pageable);
        }

        return newsPage.map(this::mapToResponse);
    }

    public NewsResponse getNewsById(Long id) {
        News news = newsRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));
        return mapToResponse(news);
    }

    @Transactional
    public NewsResponse updateNewsStatus(Long id, String toStatus) {
        News news = newsRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));
        
        News.Status fromStatus = news.getStatus();
        news.setStatus(News.Status.valueOf(toStatus.toUpperCase()));
        
        User currentUser = getCurrentUser();
        recordStatusChange(news, fromStatus.name(), toStatus, currentUser);
        
        return mapToResponse(newsRepository.save(news));
    }

    @Transactional
    public void softDeleteNews(Long id) {
        News news = newsRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));
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
        news.setIsDeleted(false);
        news.setDeletedAt(null);
        newsRepository.save(news);
    }

    @Transactional
    public void hardDeleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new ResourceNotFoundException("News not found");
        }
        newsRepository.deleteById(id);
    }

    public List<NewsHistory> getNewsHistory(Long newsId) {
        return newsHistoryRepository.findByNewsIdOrderByChangedByDesc(newsId);
    }

    private void recordStatusChange(News news, String fromStatus, String toStatus, User user) {
        NewsHistory history = NewsHistory.builder()
                .news(news)
                .changedBy(user)
                .fromStatus(fromStatus)
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

    public NewsResponse mapToResponse(News news) {
        Map<String, NewsResponse.NewsTranslationResponse> translations = news.getTranslations().stream()
                .collect(Collectors.toMap(
                    NewsTranslation::getLang,
                    t -> NewsResponse.NewsTranslationResponse.builder()
                            .id(t.getId())
                            .lang(t.getLang())
                            .title(t.getTitle())
                            .slug(t.getSlug())
                            .summary(t.getSummary())
                            .content(t.getContent())
                            .metaTitle(t.getMetaTitle())
                            .metaDescription(t.getMetaDescription())
                            .build()
                ));

        List<String> tags = news.getTags() != null ? 
                news.getTags().stream().map(Tag::getCode).collect(Collectors.toList()) : 
                Collections.emptyList();

        return NewsResponse.builder()
                .id(news.getId())
                .authorId(news.getAuthor().getId())
                .authorName(news.getAuthor().getFullName())
                .categoryId(news.getCategory() != null ? news.getCategory().getId() : null)
                .categoryTitle(null) // Can be populated if needed
                .coverMediaId(news.getCoverMedia() != null ? news.getCoverMedia().getId() : null)
                .coverMediaUrl(news.getCoverMedia() != null ? news.getCoverMedia().getUrl() : null)
                .status(news.getStatus())
                .isFeatured(news.getIsFeatured())
                .isDeleted(news.getIsDeleted())
                .publishAt(news.getPublishAt())
                .unpublishAt(news.getUnpublishAt())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .translations(translations)
                .tags(tags)
                .build();
    }
}

