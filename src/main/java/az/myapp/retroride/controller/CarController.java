package az.myapp.retroride.controller;
import az.myapp.retroride.dto.request.CarRequestDto;
import az.myapp.retroride.dto.response.CarResponseDto;
import az.myapp.retroride.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;


    @GetMapping
    public ResponseEntity<List<CarResponseDto>> getAllCars() {
        return ResponseEntity.ok(carService.getAllActiveCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }


    @GetMapping("/my")
    public ResponseEntity<List<CarResponseDto>> getMyCars() {
        return ResponseEntity.ok(carService.getMyCars());
    }


    @PostMapping
    public ResponseEntity<CarResponseDto> createCar(@RequestBody CarRequestDto request) {
        return ResponseEntity.ok(carService.createCar(request));
    }


    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDto> updateCar(@PathVariable Long id,
                                                 @RequestBody CarRequestDto request) {
        return ResponseEntity.ok(carService.updateCar(id, request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build(); // 204
    }
}