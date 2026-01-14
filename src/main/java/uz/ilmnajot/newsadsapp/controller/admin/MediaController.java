package uz.ilmnajot.newsadsapp.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.Media;
import uz.ilmnajot.newsadsapp.service.MediaService;

@RestController
@RequestMapping("/api/v1/admin/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    /**
    * UPLOAD Media
    * */
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse uploadMedia(@RequestParam("file") MultipartFile file) {
        return mediaService.uploadMedia(file);
    }

    /**
     * DELETE Media
     * */
    @DeleteMapping("/{id}")
    public ApiResponse deleteMedia(@PathVariable Long id) {
        return mediaService.deleteMedia(id);
    }
}

