package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ads_placement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdsPlacement extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;
}

