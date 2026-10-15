package com.MedLinks.MedLinks_10.Service;

import com.MedLinks.MedLinks_10.DTO.MedicineRequest;
import com.MedLinks.MedLinks_10.DTO.MedicineResponse;

import java.util.List;

public interface MedicineService {

    MedicineResponse addMedicine(
            MedicineRequest request,
            String sellerEmail
    );

    MedicineResponse updateMedicine(
            Long medicineId,
            MedicineRequest request,
            String sellerEmail
    );

    void deleteMedicine(
            Long medicineId,
            String sellerEmail
    );

    MedicineResponse getMedicineById(
            Long id
    );

    List<MedicineResponse> getAllMedicines();

    List<MedicineResponse> getMedicinesBySeller(
            String sellerEmail
    );

    List<MedicineResponse> searchMedicines(
            String name
    );

    List<MedicineResponse> getMedicinesByCategory(
            String category
    );
}