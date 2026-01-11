package uz.ilmnajot.newsadsapp.mapper;

import org.springframework.stereotype.Component;
import uz.ilmnajot.newsadsapp.dto.NewsPublicResponse;
import uz.ilmnajot.newsadsapp.dto.NewsResponse;
import uz.ilmnajot.newsadsapp.entity.CategoryTranslation;
import uz.ilmnajot.newsadsapp.entity.News;
import uz.ilmnajot.newsadsapp.entity.NewsTranslation;
import uz.ilmnajot.newsadsapp.entity.Tag;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NewsMapper {
    public NewsResponse toDto(News news) {
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
                .categoryTitle(null)
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
    public List<NewsResponse> toDto(List<News> news){
        if (news.isEmpty()){
            return Collections.emptyList();
        }
        return news
                .stream()
                .map(this::toDto)
                .toList();
    }


    /**
     * Map to public DTO (faqat 1 ta til)
     */
    public NewsPublicResponse toPublicDto(News news, String lang) {

        // Get translation for requested language
        NewsTranslation translation = news.getTranslations().stream()
                .filter(t -> t.getLang().equals(lang))
                .findFirst()
                .orElse(null);

        if (translation == null) {
            return null;  // Translation not found
        }

        return NewsPublicResponse.builder()
                .id(news.getId())
                .title(translation.getTitle())
                .slug(translation.getSlug())
                .summary(translation.getSummary())
                .content(translation.getContent())
                .metaTitle(translation.getMetaTitle())
                .metaDescription(translation.getMetaDescription())
                .coverImageUrl(news.getCoverMedia() != null ? news.getCoverMedia().getUrl() : null)
                .categoryId(news.getCategory() != null ? news.getCategory().getId() : null)
                .categoryTitle(getCategoryTitle(news, lang))
                .categorySlug(getCategorySlug(news, lang))
                .tags(news.getTags().stream()
                        .map(Tag::getCode)
                        .collect(Collectors.toSet()))
                .isFeatured(news.getIsFeatured())
                .publishedAt(news.getPublishAt() != null ? news.getPublishAt() : news.getCreatedAt())
                .build();
    }

    private String getCategoryTitle(News news, String lang) {
        if (news.getCategory() == null) return null;
        return news.getCategory().getTranslations().stream()
                .filter(t -> t.getLang().equals(lang))
                .findFirst()
                .map(CategoryTranslation::getTitle)
                .orElse(null);
    }

    private String getCategorySlug(News news, String lang) {
        if (news.getCategory() == null) return null;
        return news.getCategory().getTranslations().stream()
                .filter(t -> t.getLang().equals(lang))
                .findFirst()
                .map(CategoryTranslation::getSlug)
                .orElse(null);
    }
}
