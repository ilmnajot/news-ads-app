package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category_translation", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"category_id", "lang"}),
           @UniqueConstraint(columnNames = {"slug", "lang"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTranslation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 5)
    private String lang;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;
}

