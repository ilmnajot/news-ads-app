package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.ilmnajot.newsadsapp.entity.base.BaseEntity;
import uz.ilmnajot.newsadsapp.entity.base.BaseLongEntity;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseLongEntity {
    
    @Column(nullable = false, unique = true)
    private String name;

    private Boolean deleted=false;

}

