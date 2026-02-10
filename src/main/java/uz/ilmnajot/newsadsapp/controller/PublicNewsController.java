package uz.ilmnajot.newsadsapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.annotation.RateLimit;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.filter.NewsFilter;
import uz.ilmnajot.newsadsapp.service.PublicNewsService;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/public/news")
@RequiredArgsConstructor
public class PublicNewsController {
    private final PublicNewsService publicNewsService;

    // Get public news list
// GET /api/v1/public/news?lang=uz&page=0&size=10&category=1&tag=futbol
// PUBLIC NEWS SEARCH - 60 per minute
// Allows more requests for public

    @RateLimit(
            limit = 60,
            duration = 1,
            timeUnit = TimeUnit.MINUTES,
            message = "Too many requests"
    )
    @GetMapping
    public ApiResponse getPublicNews(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "uz") String lang,
            @RequestParam(value = "tag", required = false) String tag,
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
        return publicNewsService.getPublicNews(filter, PageRequest.of(page, size, Sort.by("publishAt").descending()));
    }

    // Get single news by slug
// GET /api/v1/public/news/ozbekiston-qatarni-yengdi?lang=uz
    @GetMapping("/{slug}")
    public ApiResponse getNewsBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "uz") String lang) {
        return publicNewsService.getNewsBySlug(slug, lang);
    }

    @GetMapping("/categories")
    public ApiResponse getPublicCategories(
            @RequestParam(defaultValue = "uz") String lang) {
        return publicNewsService.getPublicCategories(lang);
    }

    @GetMapping("/tags")
    // getPublicTags
    public ApiResponse getPublicTags() {
        return publicNewsService.getPublicTags();
    }
}
