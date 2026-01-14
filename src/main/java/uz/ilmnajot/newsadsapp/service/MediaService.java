package uz.ilmnajot.newsadsapp.service;

import org.springframework.web.multipart.MultipartFile;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;

public interface MediaService {
    ApiResponse uploadMedia(MultipartFile file);
    ApiResponse deleteMedia(Long id);
}
