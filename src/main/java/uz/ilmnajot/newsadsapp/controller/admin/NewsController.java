package uz.ilmnajot.newsadsapp.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.dto.request.NewsCreateRequest;
import uz.ilmnajot.newsadsapp.dto.response.NewsResponse;
import uz.ilmnajot.newsadsapp.entity.NewsHistory;
import uz.ilmnajot.newsadsapp.service.NewsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getAllNews(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String lang,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<NewsResponse> news = newsService.getNews(pageable, status, authorId, categoryId, tag, lang);
        return ResponseEntity.ok(news);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        NewsResponse news = newsService.getNewsById(id);
        return ResponseEntity.ok(news);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'AUTHOR')")
    public ResponseEntity<NewsResponse> createNews(@Valid @RequestBody NewsCreateRequest request) {
        NewsResponse news = newsService.createNews(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(news);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<NewsResponse> updateStatus(@PathVariable Long id, 
                                                      @RequestBody Map<String, String> request) {
        String toStatus = request.get("to_status");
        NewsResponse news = newsService.updateNewsStatus(id, toStatus);
        return ResponseEntity.ok(news);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.softDeleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Void> restoreNews(@PathVariable Long id) {
        newsService.restoreNews(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> hardDeleteNews(@PathVariable Long id) {
        newsService.hardDeleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<NewsHistory>> getNewsHistory(@PathVariable Long id) {
        List<NewsHistory> history = newsService.getNewsHistory(id);
        return ResponseEntity.ok(history);
    }
}

