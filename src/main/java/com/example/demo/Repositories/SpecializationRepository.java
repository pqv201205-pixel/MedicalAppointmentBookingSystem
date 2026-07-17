package com.example.demo.Repositories;

import com.example.demo.Entities.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Integer> {
    boolean existsByName(String name);
    boolean existsByNameAndSpecializationIdNot(String name, Integer specializationId); // Dùng cho hàm Update
}