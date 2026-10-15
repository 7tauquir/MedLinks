package com.MedLinks.MedLinks_10.Repository;

import com.MedLinks.MedLinks_10.Entities.Medicine;
import com.MedLinks.MedLinks_10.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findBySeller(User seller);

    List<Medicine> findByNameContainingIgnoreCase(String name);

    List<Medicine> findByCategoryIgnoreCase(String category);
}