package uz.ilmnajot.newsadsapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.dto.response.NewsResponse;
import uz.ilmnajot.newsadsapp.enums.NewsStatus;
import uz.ilmnajot.newsadsapp.repository.NewsRepository;
import uz.ilmnajot.newsadsapp.repository.NewsTranslationRepository;
import uz.ilmnajot.newsadsapp.entity.News;
import uz.ilmnajot.newsadsapp.entity.NewsTranslation;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.service.NewsService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/public/news")
@RequiredArgsConstructor
public class PublicNewsController {

    private final NewsRepository newsRepository;
    private final NewsTranslationRepository newsTranslationRepository;
    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getPublishedNews(
            @RequestParam(defaultValue = "uz") String lang,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String tag,
            @PageableDefault(size = 20) Pageable pageable) {
        
        LocalDateTime now = LocalDateTime.now();
        Page<News> newsPage = newsRepository.findPublishedNewsByLang(
                NewsStatus.PUBLISHED, lang, now, pageable);
        
        Page<NewsResponse> response = newsPage.map(newsService::mapToResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<NewsResponse> getNewsBySlug(@PathVariable String slug,
                                                       @RequestParam(defaultValue = "uz") String lang) {
        NewsTranslation translation = newsTranslationRepository.findPublishedBySlugAndLang(slug, lang)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));
        
        NewsResponse response = newsService.mapToResponse(translation.getNews());
        return ResponseEntity.ok(response);
    }
}
