package uz.ilmnajot.newsadsapp.service;

import org.springframework.data.domain.Pageable;
import uz.ilmnajot.newsadsapp.dto.NewsCreateRequest;
import uz.ilmnajot.newsadsapp.dto.NewsResponse;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.enums.NewsStatus;
import uz.ilmnajot.newsadsapp.filter.NewsFilter;

public interface NewsService {
     NewsResponse createNews(NewsCreateRequest request);
    ApiResponse getNews(Pageable pageable, NewsFilter filter);
    NewsResponse updateNewsStatus(Long id, NewsStatus newStatus);
    NewsResponse getNewsById(Long id);
    void softDeleteNews(Long id);
    ApiResponse restoreNews(Long id);
    ApiResponse hardDeleteNews(Long id);
    ApiResponse getNewsHistory(Long newsId);
}
