package uz.ilmnajot.newsadsapp.filter;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import uz.ilmnajot.newsadsapp.entity.News;
import uz.ilmnajot.newsadsapp.entity.NewsTranslation;
import uz.ilmnajot.newsadsapp.enums.NewsStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NewsFilter implements Specification<News> {

    private String keyword;
    private String lang;
    private String tag;
    private Long authorId;
    private Long categoryId;
    private NewsStatus status;
    private Boolean isFeatured;
    private Boolean isDeleted;
    private LocalDate from;
    private LocalDate to;

    // Setters
    public void setKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            this.keyword = keyword.trim();
        }
    }

    // setLang
    public void setLang(String lang) {
        if (lang != null && !lang.trim().isEmpty()) {
            this.lang = lang.trim();
        }
    }

    // setTag
    public void setTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tag = tag.trim();
        }
    }

    // setAuthorId
    public void setAuthorId(Long authorId) {
        if (authorId != null) {
            this.authorId = authorId;
        }
    }

    // setCategoryId
    public void setCategoryId(Long categoryId) {
        if (categoryId != null) {
            this.categoryId = categoryId;
        }
    }

    // setStatus
    public void setStatus(NewsStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    // setFeatured
    public void setFeatured(Boolean featured) {
        if (featured != null) {
            this.isFeatured = featured;
        }
    }

    // setDeleted
    public void setDeleted(Boolean deleted) {
        if (deleted != null) {

            this.isDeleted = deleted;
        }
    }

    // setFrom
    public void setFrom(LocalDate from) {
        if (from != null) {
            this.from = from;

        }
    }

    // setTo
    public void setTo(LocalDate to) {
        if (to != null) {
            this.to = to;

        }
    }

    @Override
    public Predicate toPredicate(@NotNull Root<News> root,
            CriteriaQuery<?> query,
            @NotNull CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();
        query.distinct(true);

        Join<News, NewsTranslation> translationJoin = null;

        // 🔥 JOIN agar lang YOKI keyword bo‘lsa
        if (lang != null || keyword != null) {
            translationJoin = root.join("translations", JoinType.INNER);
        }

        // 1️⃣ LANG FILTER
        if (lang != null && translationJoin != null) {
            predicates.add(cb.equal(translationJoin.get("lang"), lang));
        }

        // 2️⃣ KEYWORD FILTER
        if (keyword != null && translationJoin != null) {
            String pattern = "%" + keyword.toLowerCase() + "%";

            predicates.add(
                    cb.or(
                            cb.like(cb.lower(translationJoin.get("title")), pattern),
                            cb.like(cb.lower(translationJoin.get("summary")), pattern),
                            cb.like(cb.lower(translationJoin.get("content")), pattern)));
        }

        // 3️⃣ AUTHOR
        if (authorId != null) {
            predicates.add(cb.equal(root.get("author").get("id"), authorId));
        }

        // 4️⃣ CATEGORY
        if (categoryId != null) {
            predicates.add(cb.equal(root.get("category").get("id"), categoryId));
        }

        // 5️⃣ STATUS
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        // 6️⃣ FEATURED
        if (isFeatured != null) {
            predicates.add(cb.equal(root.get("isFeatured"), isFeatured));
        }

        // 7️⃣ DELETED
        predicates.add(cb.equal(root.get("isDeleted"), isDeleted != null ? isDeleted : false));

        // // 8️⃣ DATE RANGE
        // if (from != null) {
        // predicates.add(cb.greaterThanOrEqualTo(
        // root.get("createdAt"), from.atStartOfDay()));
        // }
        //
        // if (to != null) {
        // predicates.add(cb.lessThanOrEqualTo(
        // root.get("createdAt"), to.atTime(LocalTime.MAX)));
        // }

        // if (from != null || to != null)
        Path<LocalDateTime> dateField = root.get("publishAt");

        if (from != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(
                            dateField,
                            from.atStartOfDay()));
        }

        if (to != null) {
            predicates.add(
                    cb.lessThan(
                            dateField,
                            to.plusDays(1).atStartOfDay()));
        }
        // query.orderBy(...)

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}