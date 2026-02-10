package uz.ilmnajot.newsadsapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.ilmnajot.newsadsapp.dto.TagDto;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.Tag;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.mapper.TagMapper;
import uz.ilmnajot.newsadsapp.repository.TagRepository;
import uz.ilmnajot.newsadsapp.service.TagService;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;


    @Override
    // addTag
    public ApiResponse addTag(TagDto.AddTag dto) {
        if (this.tagRepository.existsByCode(dto.getCode())) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Code already exists")
                    .build();
        }
        Tag tag = new Tag();
        tag.setCode(dto.getCode());
        tag.setIsActive(true);
        this.tagRepository.save(tag);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Tag has been added")
                .build();
    }

    @Override
    // getAllTags
    public ApiResponse getAllTags(Pageable pageable) {
        Page<Tag> page = this.tagRepository.findAll(pageable);
        if (page.isEmpty()) {
            return ApiResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .data(new ArrayList<>())
                    .build();
        }
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .data(this.tagMapper.toDto(page.getContent()))
                .pages(page.getTotalPages())
                .elements(page.getTotalElements())
                .build();
    }

    @Override
    // deleteTag
    public ApiResponse deleteTag(Long tagId) {
        Tag tag = this.tagRepository.findTagByIdAndIsActiveTrue(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found!"));
        tag.setIsActive(false);
        this.tagRepository.save(tag);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Tag has been inActivated!")
                .build();
    }

    @Override
    // updateTag
    public ApiResponse updateTag(Long tagId, TagDto.UpdateTag dto) {
        Tag tag = this.tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found!"));
        this.tagMapper.toUpdate(tag, dto);
        this.tagRepository.save(tag);
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Tag has been updated")
                .build();
    }

    @Override
    // getTagById
    public ApiResponse getTagById(Long id) {
        Tag tag = this.tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("Tag has been gotten")
                .data(this.tagMapper.toDto(tag))
                .build();
    }
}
