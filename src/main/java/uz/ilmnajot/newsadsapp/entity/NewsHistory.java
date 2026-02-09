package uz.ilmnajot.newsadsapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.ilmnajot.newsadsapp.entity.base.BaseEntity;
import uz.ilmnajot.newsadsapp.entity.base.BaseLongEntity;

import java.util.Map;

@Entity
@Table(name = "news_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsHistory extends BaseLongEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Column(name = "from_status", length = 20)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 20)
    private String toStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "diff_json", columnDefinition = "jsonb")
    private Map<String, Object> diffJson;
}

