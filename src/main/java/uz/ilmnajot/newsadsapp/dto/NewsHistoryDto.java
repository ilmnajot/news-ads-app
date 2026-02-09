package uz.ilmnajot.newsadsapp.dto;

import lombok.Builder;
import lombok.Data;
import uz.ilmnajot.newsadsapp.entity.User;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class NewsHistoryDto {

    private Long id;
    private NewsResponse newsResponse;
    private User changedBy;
    private String fromStatus;
    private String toStatus;
    private Map<String, Object> diffJson;
    private LocalDateTime publishAt;
    private LocalDateTime unpublishAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;


}
