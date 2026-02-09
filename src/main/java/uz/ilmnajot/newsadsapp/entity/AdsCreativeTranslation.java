package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.ilmnajot.newsadsapp.entity.base.BaseEntity;
import uz.ilmnajot.newsadsapp.entity.base.BaseLongEntity;

@Entity
@Table(name = "ads_creative_translation",
       uniqueConstraints = @UniqueConstraint(columnNames = {"creative_id", "lang"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdsCreativeTranslation extends BaseLongEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creative_id", nullable = false)
    private AdsCreative creative;

    @Column(nullable = false, length = 5)
    private String lang;

    private String title;

    @Column(name = "alt_text", length = 255)
    private String altText;
}

