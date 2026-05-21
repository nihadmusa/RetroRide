package az.myapp.retroride.controller;

import az.myapp.retroride.service.CarImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CarImageController {

    private final CarImageService carImageService;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    // POST /api/cars/5/images — şəkil yüklə
    // multipart/form-data ilə göndərilir — form-dan fayl seçəndə belə olur
    @PostMapping("/api/cars/{carId}/images")
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable Long carId,
            @RequestParam("file") MultipartFile file) {
        try {
            carImageService.uploadImage(carId, file);
            return ResponseEntity.ok(Map.of("message", "Şəkil uğurla yükləndi"));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Şəkil saxlanılarkən xəta baş verdi"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/cars/5/images — maşının şəkil URL-lərini gətir
    @GetMapping("/api/cars/{carId}/images")
    public ResponseEntity<List<Map<String, Object>>> getImages(@PathVariable Long carId) {
        return ResponseEntity.ok(carImageService.getImageUrls(carId));
    }

    // DELETE /api/images/{imageId} — şəkili sil
    @DeleteMapping("/api/images/{imageId}")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable Long imageId) {
        try {
            carImageService.deleteImage(imageId);
            return ResponseEntity.ok(Map.of("message", "Şəkil silindi"));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Şəkil silinərkən xəta baş verdi"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/images/abc123.jpg — şəkili göstər (brauzerdə açılır)
    @GetMapping("/api/images/{fileName}")
    public ResponseEntity<Resource> serveImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Fayl növünə görə content type təyin et
            String contentType = "image/jpeg";
            if (fileName.endsWith(".png"))  contentType = "image/png";
            if (fileName.endsWith(".webp")) contentType = "image/webp";
            if (fileName.endsWith(".gif"))  contentType = "image/gif";
            if (fileName.endsWith(".avif")) contentType = "image/avif";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}