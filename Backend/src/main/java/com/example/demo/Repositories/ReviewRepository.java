package com.example.demo.Repositories;

import com.example.demo.Entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> { // Khóa chính ID đánh giá kiểu Integer

    // Hàm chuẩn Spring Data JPA bóc tách quan hệ: Tìm đánh giá theo DoctorId và sắp xếp thời gian giảm dần
    List<Review> findByDoctor_DoctorIdOrderByCreatedAtDesc(Integer doctorId);
}