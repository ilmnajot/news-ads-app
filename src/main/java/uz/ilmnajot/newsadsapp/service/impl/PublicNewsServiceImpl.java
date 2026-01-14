package uz.ilmnajot.newsadsapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ilmnajot.newsadsapp.dto.NewsPublicResponse;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.Category;
import uz.ilmnajot.newsadsapp.entity.News;
import uz.ilmnajot.newsadsapp.entity.Tag;
import uz.ilmnajot.newsadsapp.enums.NewsStatus;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.filter.NewsFilter;
import uz.ilmnajot.newsadsapp.mapper.CategoryMapper;
import uz.ilmnajot.newsadsapp.mapper.NewsMapper;
import uz.ilmnajot.newsadsapp.repository.CategoryRepository;
import uz.ilmnajot.newsadsapp.repository.NewsRepository;
import uz.ilmnajot.newsadsapp.repository.TagRepository;
import uz.ilmnajot.newsadsapp.service.PublicNewsService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PublicNewsServiceImpl implements PublicNewsService {
    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TagRepository tagRepository;

    /**
     * Get public news list with filters and cache
     */
//    @Cacheable(
//            value = "newsList",
//            key = "'lang:' + #lang +" +
//                    "':page:' + #pageable.pageNumber + " +
//                    "':size:' + #pageable.pageSize + " +
//                    "':category:' + (#categoryId != null ? #categoryId : 'all') + " +
//                    "':tag:' + (#tag != null ? #tag : 'all')",
//            unless = "#result == null || #result.data == null || #result.data.isEmpty()"
//    )
    @Cacheable(
            value = "newsList",
            key = "'lang:' + #filter.lang + " +
                    "':page:' + #pageable.pageNumber + " +
                    "':size:' + #pageable.pageSize + " +
                    "':category:' + (#filter.categoryId != null ? #filter.categoryId : 'all') + " +
                    "':tag:' + (#filter.tag != null ? #filter.tag : 'all')",
            unless = "#result == null || #result.data == null || #result.data.isEmpty()"
    )
    @Transactional(readOnly = true)
    public ApiResponse getPublicNews(
            NewsFilter filter, Pageable pageable) {

        log.info("Cache MISS - Fetching from DB with filter={}, page={}",
                filter, pageable.getPageNumber());

        // 🔥 PUBLISHED + DATE constraint majburiy
        filter.setStatus(NewsStatus.PUBLISHED);
        filter.setDeleted(false);

        Page<News> page = newsRepository.findAll(filter, pageable);

        if (page.isEmpty()) {
            return ApiResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .data(List.of())
                    .build();
        }
        String lang = filter.getLang(); // 🔥 mapping uchun

        List<NewsPublicResponse> responses = page.getContent().stream()
                .map(news -> newsMapper.toPublicDto(news, lang))
                .toList();

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(responses)
                .pages(page.getTotalPages())
                .elements(page.getTotalElements())
                .build();
    }

    /**
     * Get single news by slug
     */
    @Cacheable(
            value = "newsDetail",
            key = "'slug:' + #slug + ':lang:' + #lang",
            unless = "#result == null"
    )
    public ApiResponse getNewsBySlug(String slug, String lang) {

        log.info("Cache MISS - Fetching from DB: slug={}, lang={}", slug, lang);
        LocalDateTime now = LocalDateTime.now();
        News news = newsRepository.findPublicNewsBySlug(slug, lang, now)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .data(newsMapper.toPublicDto(news, lang))
                .build();
    }

    @Cacheable(
            value = "categories",
            key = "'lang:' + #lang",
            unless = "#result == null || #result.data.isEmpty()"
    )
    @Transactional(readOnly = true)
    public ApiResponse getPublicCategories(String lang) {

        List<Category> categories = categoryRepository.findPublicCategories(lang);

        if (categories.isEmpty()) {
            return ApiResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .data(List.of())
                    .build();
        }

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .data(
                        categories.stream()
                                .map(c -> categoryMapper.toPublicDto(c, lang))
                                .toList()
                )
                .build();
    }

    @Cacheable(
            value = "tags",
            unless = "#result == null || #result.data.isEmpty()"
    )
    @Transactional(readOnly = true)
    public ApiResponse getPublicTags() {
        List<Tag> tags = tagRepository.findAllByIsActiveTrue();

        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .data(
                        tags.stream()
                                .map(Tag::getCode)
                                .toList()
                )
                .build();
    }

}
