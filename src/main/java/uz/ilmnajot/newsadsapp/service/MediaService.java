package uz.ilmnajot.newsadsapp.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.ilmnajot.newsadsapp.entity.Media;
import uz.ilmnajot.newsadsapp.entity.User;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.repository.MediaRepository;
import uz.ilmnajot.newsadsapp.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    private final MediaRepository mediaRepository;
    private final MinioClient minioClient;
    private final UserRepository userRepository;

    @Value("${app.s3.bucket:media}")
    private String bucketName;

    @Value("${app.s3.endpoint:http://localhost:9000}")
    private String endpoint;

    public Media uploadMedia(MultipartFile file, String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String storageKey = UUID.randomUUID().toString() + extension;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storageKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String url = endpoint + "/" + bucketName + "/" + storageKey;

            Media media = Media.builder()
                    .storageKey(storageKey)
                    .url(url)
                    .mimeType(file.getContentType())
                    .size(file.getSize())
                    .owner(user)
                    .isPublic(true)
                    .build();

            return mediaRepository.save(media);
        } catch (Exception e) {
            log.error("Error uploading media", e);
            throw new RuntimeException("Failed to upload media: " + e.getMessage());
        }
    }

    public void deleteMedia(Long id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found"));

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(media.getStorageKey())
                            .build()
            );
            mediaRepository.delete(media);
        } catch (Exception e) {
            log.error("Error deleting media", e);
            throw new RuntimeException("Failed to delete media: " + e.getMessage());
        }
    }
}

