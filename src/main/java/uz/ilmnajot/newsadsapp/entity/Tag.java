package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "is_active")
    private Boolean isActive = true;
}

