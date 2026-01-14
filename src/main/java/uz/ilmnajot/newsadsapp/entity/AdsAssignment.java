package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.ilmnajot.newsadsapp.entity.base.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ads_assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdsAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placement_id", nullable = false)
    private AdsPlacement placement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private AdsCampaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creative_id", nullable = false)
    private AdsCreative creative;

    @Column
    @Builder.Default
    private Integer weight = 100;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lang_filter", columnDefinition = "jsonb")
    private List<String> langFilter;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "category_filter", columnDefinition = "jsonb")
    private List<Long> categoryFilter;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}

