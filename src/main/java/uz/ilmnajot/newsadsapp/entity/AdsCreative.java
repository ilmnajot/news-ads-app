package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.ilmnajot.newsadsapp.enums.CreativeType;

import java.util.List;

@Entity
@Table(name = "ads_creative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdsCreative extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private AdsCampaign campaign;

    @Enumerated(EnumType.STRING)
    private CreativeType type;

    @Column(name = "landing_url", length = 1000)
    private String landingUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_media_id")
    private Media imageMedia;

    @Column(name = "html_snippet", columnDefinition = "TEXT")
    private String htmlSnippet;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "creative", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdsCreativeTranslation> translations;

    @OneToMany(mappedBy = "creative", cascade = CascadeType.ALL)
    private List<AdsAssignment> assignments;
}

