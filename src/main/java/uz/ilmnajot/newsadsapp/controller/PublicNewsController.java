package uz.ilmnajot.newsadsapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.dto.NewsPublicResponse;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.filter.NewsFilter;
import uz.ilmnajot.newsadsapp.repository.NewsRepository;
import uz.ilmnajot.newsadsapp.repository.NewsTranslationRepository;
import uz.ilmnajot.newsadsapp.service.NewsService;
import uz.ilmnajot.newsadsapp.service.PublicNewsService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/public/news")
@RequiredArgsConstructor
public class PublicNewsController {

    private final NewsRepository newsRepository;
    private final NewsTranslationRepository newsTranslationRepository;
    private final NewsService newsService;
    private final PublicNewsService publicNewsService;

    /**
     * Get public news list
     * GET /api/v1/public/news?lang=uz&page=0&size=10&category=1&tag=futbol
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getPublicNews(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "uz") String lang,
            @RequestParam(value = "tag",required = false) String tag,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        NewsFilter filter = new NewsFilter();
        filter.setKeyword(keyword);
        filter.setLang(lang);
        filter.setTag(tag);
        filter.setCategoryId(categoryId);
        filter.setFrom(from);
        filter.setTo(to);

        ApiResponse apiResponse = publicNewsService.getPublicNews(filter, PageRequest.of(page, size, Sort.by("publishAt").descending()));
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    /**
     * Get single news by slug
     * GET /api/v1/public/news/ozbekiston-qatarni-yengdi?lang=uz
     */
    @GetMapping("/{slug}")
    public ResponseEntity<NewsPublicResponse> getNewsBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "uz") String lang) {

        NewsPublicResponse news = publicNewsService.getNewsBySlug(slug, lang);

        return ResponseEntity.ok(news);
    }
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse> getPublicCategories(
            @RequestParam(defaultValue = "uz") String lang) {
        ApiResponse apiResponse = publicNewsService.getPublicCategories(lang);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
    @GetMapping("/tags")
    public ResponseEntity<ApiResponse> getPublicTags() {
        ApiResponse apiResponse = publicNewsService.getPublicTags();
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
