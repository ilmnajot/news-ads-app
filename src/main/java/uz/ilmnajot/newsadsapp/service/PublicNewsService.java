package uz.ilmnajot.newsadsapp.service;

import org.springframework.data.domain.Pageable;
import uz.ilmnajot.newsadsapp.dto.NewsPublicResponse;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.filter.NewsFilter;

public interface PublicNewsService {
    ApiResponse getPublicNews(NewsFilter filter, Pageable pageable);

    ApiResponse getNewsBySlug(String slug, String lang);

    ApiResponse getPublicCategories(String lang);
    ApiResponse getPublicTags();
}
