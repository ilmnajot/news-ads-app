package uz.ilmnajot.newsadsapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ilmnajot.newsadsapp.annotation.RateLimit;
import uz.ilmnajot.newsadsapp.dto.common.ApiResponse;
import uz.ilmnajot.newsadsapp.entity.AdsAssignment;
import uz.ilmnajot.newsadsapp.entity.AdsCreative;
import uz.ilmnajot.newsadsapp.repository.AdsAssignmentRepository;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.service.AdsAssignmentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/public/ads")
@RequiredArgsConstructor
public class PublicAdsController {

    private final AdsAssignmentService adsAssignmentService;

    @RateLimit(
            limit = 5,
            duration = 1,
            timeUnit = TimeUnit.MINUTES,
            message = "Too many attempts"
    )
    @GetMapping("/{placementCode}")
    public ApiResponse getAd(@PathVariable String placementCode,
                                             @RequestParam(defaultValue = "uz") String lang,
                                             @RequestParam(required = false) Long categoryId) {
        return this.adsAssignmentService.findActiveAssignmentsByPlacement(placementCode, lang, categoryId);
    }
}
