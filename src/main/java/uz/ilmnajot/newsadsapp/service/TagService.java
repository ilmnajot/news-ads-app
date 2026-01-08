package uz.ilmnajot.newsadsapp.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uz.ilmnajot.newsadsapp.dto.TagDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;

public interface TagService {
    ApiResponse addTag(TagDto.AddTag dto);
    ApiResponse getAllTags(Pageable pageable);
    ApiResponse deleteTag(Long tagId);
    ApiResponse updateTag(Long tagId, TagDto.UpdateTag dto);
    ApiResponse getTagById(Long id);
}
