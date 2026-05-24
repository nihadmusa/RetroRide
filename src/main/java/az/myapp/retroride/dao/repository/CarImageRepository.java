package az.myapp.retroride.dao.repository;

import az.myapp.retroride.dao.entity.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarImageRepository extends JpaRepository<CarImage, Long> {


    List<CarImage> findByCarIdOrderBySortOrderAsc(Long carId);


    int countByCarId(Long carId);


    void deleteAllByCarId(Long carId);
}