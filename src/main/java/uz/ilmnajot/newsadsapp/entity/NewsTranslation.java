package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "news_translation",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"news_id", "lang"}),
           @UniqueConstraint(columnNames = {"slug", "lang"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsTranslation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Column(nullable = false, length = 5)
    private String lang;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 500)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "meta_title", length = 255)
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;
}

