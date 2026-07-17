package com.example.demo.Repositories;

import com.example.demo.Entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    Optional<Doctor> findByUser_UserId(Integer userId);

    List<Doctor> findBySpecialization_Name(String name);

    List<Doctor> findBySpecialization_NameAndExperienceYearsGreaterThanEqual(
            String name,
            Integer years);

    @Query("SELECT COUNT(d) FROM Doctor d")
    long countTotalDoctors();
}