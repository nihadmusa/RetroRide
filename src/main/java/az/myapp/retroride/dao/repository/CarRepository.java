package az.myapp.retroride.dao.repository;

import az.myapp.retroride.dao.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository
        extends JpaRepository<Car,Long> {

    List<Car> findByStatusOrderByCreatedAtDesc(Car.CarStatus status);


    List<Car> findByUserId(Long userId);


    List<Car> findByBrandContainingIgnoreCase(String brand);
}
