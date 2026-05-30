package az.myapp.retroride.service;

import az.myapp.retroride.dao.repository.CarImageRepository;
import az.myapp.retroride.dao.entity.CarImage;
import az.myapp.retroride.dao.entity.Car;
import az.myapp.retroride.dao.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarImageService {

    private final CarImageRepository carImageRepository;
    private final CarRepository carRepository;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    public CarImage uploadImage(Long carId, MultipartFile file) throws IOException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }

        String key = "images/" + UUID.randomUUID() + ext;

        // S3-ə yüklə
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        // S3 URL
        String url = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;

        int order = carImageRepository.findByCarIdOrderBySortOrderAsc(carId).size();

        CarImage image = CarImage.builder()
                .car(car)
                .fileName(url)
                .sortOrder(order)
                .build();

        return carImageRepository.save(image);
    }

    public List<Map<String, Object>> getImageUrls(Long carId) {
        return carImageRepository.findByCarIdOrderBySortOrderAsc(carId)
                .stream()
                .map(img -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", img.getId());
                    String fn = img.getFileName();
                    map.put("url", fn.startsWith("http") ? fn : "/api/images/" + fn);
                    return map;
                })
                .toList();
    }

    public void deleteImage(Long imageId) {
        CarImage image = carImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        String fileName = image.getFileName();

        // S3-dən sil
        if (fileName.startsWith("http")) {
            String key = fileName.substring(fileName.indexOf("images/"));
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        }

        carImageRepository.delete(image);
    }
}