package com.example.demo.Repositories;

import com.example.demo.Entities.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id); // Dùng cho hàm Update
}