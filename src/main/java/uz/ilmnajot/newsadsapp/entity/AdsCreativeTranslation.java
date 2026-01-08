package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ads_creative_translation",
       uniqueConstraints = @UniqueConstraint(columnNames = {"creative_id", "lang"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdsCreativeTranslation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creative_id", nullable = false)
    private AdsCreative creative;

    @Column(nullable = false, length = 5)
    private String lang;

    @Column(length = 255)
    private String title;

    @Column(name = "alt_text", length = 255)
    private String altText;
}

