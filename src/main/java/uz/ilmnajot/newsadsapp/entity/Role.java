package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity{
    
    @Column(nullable = false, unique = true)
    private String name;

    private Boolean deleted=false;

}

