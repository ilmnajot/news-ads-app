package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.ilmnajot.newsadsapp.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ads_campaign")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdsCampaign extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String advertiser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "daily_cap_impressions")
    private Integer dailyCapImpressions;

    @Column(name = "daily_cap_clicks")
    private Integer dailyCapClicks;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<AdsCreative> creatives;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<AdsAssignment> assignments;
}

