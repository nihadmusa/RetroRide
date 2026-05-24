package az.myapp.retroride.dao.repository;

import az.myapp.retroride.dao.utility.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository
        extends JpaRepository<Car,Long> {

    List<Car> findByStatus(Car.CarStatus status);


    List<Car> findByUserId(Long userId);


    List<Car> findByBrandContainingIgnoreCase(String brand);
}
