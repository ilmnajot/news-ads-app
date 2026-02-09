package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.ilmnajot.newsadsapp.entity.base.BaseEntity;
import uz.ilmnajot.newsadsapp.entity.base.BaseLongEntity;

@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag extends BaseLongEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "is_active")
    private Boolean isActive = true;
}

