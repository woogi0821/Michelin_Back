package com.simplecoding.michelin_back.restaurant.controller;

import com.simplecoding.michelin_back.common.ApiResponse;
import com.simplecoding.michelin_back.restaurant.service.RestaurantImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantImageController {

    private final RestaurantImageService restaurantImageService;

    // 이미지 업로드
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "isMain", defaultValue = "false") boolean isMain) {
        try {
            String imageUrl = restaurantImageService.uploadImage(id, file, isMain);
            return ResponseEntity.ok(ApiResponse.success(imageUrl));
        } catch (Exception e) {
            log.error("[이미지 업로드 오류] : {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.fail("이미지 업로드에 실패했습니다."));
        }
    }

    // 이미지 삭제
    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<ApiResponse<String>> deleteImage(
            @PathVariable Long id,
            @PathVariable Long imageId) {
        try {
            restaurantImageService.deleteImage(imageId);
            return ResponseEntity.ok(ApiResponse.success("이미지가 삭제되었습니다."));
        } catch (Exception e) {
            log.error("[이미지 삭제 오류] : {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.fail("이미지 삭제에 실패했습니다."));
        }
    }

    // 이미지 목록 조회
    @GetMapping("/{id}/images")
    public ResponseEntity<ApiResponse<?>> getImages(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(restaurantImageService.getImages(id)));
    }
}