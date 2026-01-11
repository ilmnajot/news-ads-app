package uz.ilmnajot.newsadsapp.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.ilmnajot.newsadsapp.dto.NewsHistoryDto;
import uz.ilmnajot.newsadsapp.dto.NewsResponse;
import uz.ilmnajot.newsadsapp.entity.NewsHistory;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class NewsHistoryMapper {

    private final NewsMapper newsMapper;

    public NewsHistoryDto toDto(NewsHistory history) {
        NewsResponse dto = newsMapper.toDto(history.getNews());
        return NewsHistoryDto.builder()
                .id(history.getId())
                .newsResponse(dto)
                .fromStatus(history.getFromStatus())
                .toStatus(history.getToStatus())
                .diffJson(history.getDiffJson())
                .publishAt(history.getNews().getPublishAt())
                .unpublishAt(history.getNews().getUnpublishAt())
                .createdAt(history.getCreatedAt())
                .updatedAt(history.getUpdatedAt())
                .createdBy(history.getCreatedBy())
                .updatedBy(history.getUpdatedBy())
                .build();

    }

    public List<NewsHistoryDto> toDto(List<NewsHistory> historyList) {
        if (historyList.isEmpty()) {
            return new ArrayList<>();
        }
        return historyList
                .stream()
                .map(this::toDto)
                .toList();
    }
}
