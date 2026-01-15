package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.annotation.RateLimit;
import uz.ilmnajot.newsadsapp.dto.NewsCreateRequest;
import uz.ilmnajot.newsadsapp.dto.NewsResponse;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.enums.NewsStatus;
import uz.ilmnajot.newsadsapp.filter.NewsFilter;
import uz.ilmnajot.newsadsapp.service.NewsService;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/admin/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @RateLimit(
            limit = 30,
            duration = 1,
            timeUnit = TimeUnit.MINUTES,
            message = "Too many requests"
    )
    @GetMapping("/get-all")
    public ApiResponse getAllNews(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "lang", defaultValue = "uz", required = false) String lang,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "authorId", required = false) Long authorId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) NewsStatus status,
            @RequestParam(value = "isFeatured", required = false) Boolean isFeatured,
            @RequestParam(value = "isDeleted", required = false) Boolean isDeleted,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        NewsFilter filter = new NewsFilter();
        filter.setKeyword(keyword);
        filter.setLang(lang);
        filter.setTag(tag);
        filter.setAuthorId(authorId);
        filter.setCategoryId(categoryId);
        filter.setStatus(status);
        filter.setFeatured(isFeatured);
        filter.setDeleted(isDeleted);
        filter.setFrom(from);
        filter.setTo(to);
        return this.newsService.getNews(PageRequest.of(page, size), filter);
    }

    //done
    @GetMapping("/{id}")
    public NewsResponse getNewsById(@PathVariable Long id) {
        return this.newsService.getNewsById(id);
    }

    //done
    @RateLimit(
            limit = 10,
            duration = 1,
            timeUnit = TimeUnit.MINUTES,
            message = "Too many requests"
    )
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public NewsResponse addNews(@Valid @RequestBody NewsCreateRequest request) {
        return this.newsService.createNews(request);
    }

    //done
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public NewsResponse updateStatus(@PathVariable Long id,
                                     @RequestParam NewsStatus status) {
        return newsService.updateNewsStatus(id, status);
    }

    //done
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.softDeleteNews(id);
        return ResponseEntity.noContent().build();
    }

    //done
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ApiResponse restoreNews(@PathVariable Long id) {
        return newsService.restoreNews(id);
    }

    //done
    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse hardDeleteNews(@PathVariable Long id) {
        return newsService.hardDeleteNews(id);
    }

    //done
    @GetMapping("/{id}/history")
    public ApiResponse getNewsHistory(@PathVariable Long id) {
        return newsService.getNewsHistory(id);
    }
}

