package uz.ilmnajot.newsadsapp.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.ilmnajot.newsadsapp.entity.Media;
import uz.ilmnajot.newsadsapp.service.MediaService;

@RestController
@RequestMapping("/api/v1/admin/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Media> uploadMedia(@RequestParam("file") MultipartFile file) {
        Media media = mediaService.uploadMedia(file);
        return ResponseEntity.ok(media);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }
}

